(ns build
  (:require [monkey.ci.build
             [api :as api]
             [core :as bc]
             [v2 :as m]]
            [monkey.ci.plugin
             [clj :as clj]
             [infra :as infra]
             [kaniko :as kaniko]
             [pushover :as p]]))

(def clj-img "docker.io/clojure:tools-deps-bullseye-slim")

(defn get-env
  "Determines deployment environment from the build info"
  [ctx]
  (if (bc/tag ctx) :prod :staging))

(def config-by-env
  {:prod
   {:base-url "monkeyci.com"}
   :staging
   {:base-url "staging.monkeyci.com"}})

(defn touched? [re]
  (fn [ctx]
    (bc/touched? ctx re)))

;; TODO Also test/deploy common if it's a release

(def common-changed?
  "True if any of the files in the `common/` dir have changed"
  (touched? #"^common/.*"))

(def common-published?
  (every-pred common-changed? bc/main-branch?))

(defn- clj-cmd
  "Runs a clojure command"
  [job-id cache-id cmd]
  (let [m2 (str ".m2-" cache-id)]
    (-> (m/container-job job-id)
        (m/image clj-img)
        (m/caches (m/cache (str "mvn-" cache-id) m2))
        (m/script [(format "clojure -Sdeps '{:mvn/local-repo \"%s\"}' %s" m2 cmd)]))))

(defn run-tests
  "Runs tests for given site"
  [id & [{:keys [alias] deps :dependencies}]]
  (let [art (str id "-junit")
        junit "junit.xml"]
    (fn [ctx]
      (letfn [(maybe-add-deps [job]
                (let [d (when deps (deps ctx))]
                  (cond-> job
                    d (m/depends-on d))))]
        (-> (clj-cmd
             (str "test-" id)
             id
             (format "-X%s:test:junit" (or (some-> alias str) "")))
            (m/work-dir id)
            (m/save-artifacts (m/artifact art junit))
            (assoc :junit {:artifact-id art
                           :path junit})
            (maybe-add-deps))))))

(defn test-common [ctx]
  (when (common-changed? ctx)
    ((run-tests "common") ctx)))

(defn deploy-common [ctx]
  (let [conf {:publish-job-id "deploy-common"
              :test-job-id "test-common"}]
    (when (common-changed? ctx)
      (some-> ((clj/deps-publish conf) ctx)
              (assoc :work-dir "common")))))

(defn depends-on-common [ctx]
  (when (common-published? ctx)
    ["deploy-common"]))

(def test-site (run-tests "site" {:dependencies depends-on-common}))
(def test-docs (run-tests "docs" {:alias :template
                                  :dependencies depends-on-common}))

(defn build
  "Builds the website and docs files"
  [id alias artifact ctx]
  (-> (clj-cmd
       (str "build-" id)
       id
       (format "-X%s '%s'" (str alias) (pr-str {:config (get config-by-env (get-env ctx))})))
      (m/save-artifacts (m/artifact id artifact))
      (m/depends-on (cond-> [(str "test-" id)]
                      (common-published? ctx) (conj "deploy-common")))
      (m/work-dir id)))

(def build-site (partial build "site" :build "target"))
(def build-docs-theme (partial build "docs" :template "themes/space"))

(def build-docs-site
  (-> (clj-cmd
       "build-docs-site"
       "docs-site"
       "-M:cryogen:build")
      (m/work-dir "docs")
      (m/save-artifacts (m/artifact "docs" "public"))
      (m/depends-on "build-docs")))

(def img-base "fra.ocir.io/frjdhmocn5qi/website")

(def release? (comp some? bc/tag))
(def build-image? (some-fn bc/main-branch? bc/tag))

(defn img-version
  "Determines the image version to use in the tag"
  [ctx]
  (or (some->> (bc/tag ctx) (str "release-"))
      (get-in ctx [:build :build-id])))

(defn image
  "Generates the container image"
  [ctx]
  (when (build-image? ctx)
    (-> (kaniko/image {:target-img (str img-base ":" (img-version ctx))} ctx)
        (m/depends-on ["build-site" "build-docs-site"])
        (m/restore-artifacts [(m/artifact "site" "site/target")
                              (m/artifact "docs" "docs/public")]))))

(defn deploy [ctx]
  (when (and (build-image? ctx) (not (release? ctx)))
    (bc/action-job
     "deploy"
     (fn [ctx]
       (if-let [token (get (api/build-params ctx) "github-token")]
         ;; Patch the kustomization file
         (if (infra/patch+commit! (infra/make-client token)
                                  (get-env ctx)
                                  {"website" (img-version ctx)})
           bc/success
           (assoc bc/failure :message "Unable to patch version in infra repo"))
         (assoc bc/failure :message "No github token provided")))
     {:dependencies ["image"]})))

(defn notify [ctx]
  (when (release? ctx)
    (p/pushover-msg {:msg (str "Website version " (bc/tag ctx) " has been published.")
                     :dependencies ["image"]})))

[test-common
 deploy-common
 test-site
 test-docs
 build-site
 build-docs-theme
 build-docs-site
 image
 deploy
 notify]
