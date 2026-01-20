(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.plugin
             [clj :as clj]
             [infra :as infra]
             [kaniko :as kaniko]
             [pushover :as p]]))

(def clj-img "docker.io/clojure:tools-deps-bullseye-slim")

(defn get-env
  "Determines deployment environment from the build info"
  [ctx]
  (if (m/tag ctx) :prod :staging))

(def config-by-env
  {:prod
   {:base-url "monkeyci.com"
    :path-prefix "/"
    :svg-path "dev-resources/svg"}
   :staging
   {:base-url "staging.monkeyci.com"
    :path-prefix "/"
    :svg-path "dev-resources/svg"}})

(defn touched? [re]
  (fn [ctx]
    (m/touched? ctx re)))

(def manual? (comp (partial = :api) m/source))
(def release? (comp some? m/tag))

(def common-changed?
  "True if any of the files in the `common/` dir have changed"
  (some-fn manual? (touched? #"^common/.*")))

(def publish-common?
  (some-fn (every-pred common-changed? m/main-branch?)
           release?))

(def common-published? publish-common?)

(defn- clj-cmds
  "Runs one or more clojure commands"
  [job-id cache-id & cmds]
  (let [m2 (str ".m2-" cache-id)]
    (-> (m/container-job job-id)
        (m/image clj-img)
        (m/caches (m/cache (str "mvn-" cache-id) m2))
        (m/script (->> cmds
                       (mapv (partial format "clojure -Sdeps '{:mvn/local-repo \"%s\"}' %s" m2)))))))

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
        (-> (clj-cmds
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
    (when (publish-common? ctx)
      (some-> ((clj/deps-publish conf) ctx)
              (assoc :work-dir "common")))))

(defn depends-on-common [ctx]
  (when (common-published? ctx)
    ["deploy-common"]))

(def test-site (run-tests "site"))
(def test-docs (run-tests "docs"))

(defn build
  "Builds static website files"
  [id aliases artifact ctx]
  (-> (apply clj-cmds
       (str "build-" id)
       id
       (map #(format "-X%s '%s'" (str %) (pr-str {:config (get config-by-env (get-env ctx))}))
            aliases))
      (m/save-artifacts (m/artifact id artifact))
      (m/depends-on (cond-> [(str "test-" id)]
                      (common-published? ctx) (conj "deploy-common")))
      (m/work-dir id)))

(def build-site (partial build "site" [:build] "target"))
(def build-docs (partial build "docs" [:build] "target/site"))

(def img-base "rg.fr-par.scw.cloud/monkeyci/website")

(def release? (comp some? m/tag))
(def build-image? (some-fn m/main-branch? m/tag))

(defn img-version
  "Determines the image version to use in the tag"
  [ctx]
  (or (some->> (m/tag ctx) (str "release-"))
      (get-in ctx [:build :build-id])))

(defn image
  "Generates the container image"
  [ctx]
  (when (build-image? ctx)
    (kaniko/multi-platform-image-jobs
     {:target-img (str img-base ":" (img-version ctx))
      :archs (m/archs ctx)
      :image {:container-opts
              {:dependencies ["build-site" "build-docs"]
               :restore-artifacts
               [(m/artifact "site" "site/target")
                (m/artifact "docs" "docs/target/site")]}}})))

(defn deploy [ctx]
  (when (and (build-image? ctx) (not (release? ctx)))
    (-> (m/action-job
         "deploy"
         (fn [ctx]
           (if-let [token (get (m/build-params ctx) "github-token")]
             (try
               ;; Patch the kustomization file
               ;; TODO Patch scw-images version instead
               (if (infra/patch+commit! (infra/make-client token)
                                        (get-env ctx)
                                        {"website" (img-version ctx)})
                 m/success
                 (assoc m/failure :message "Unable to patch version in infra repo"))
               (catch Exception ex
                 ;; Print response
                 (println "Github request failed:" (:response (ex-data ex)))
                 (m/with-message m/failure (ex-message ex))))
             (assoc m/failure :message "No github token provided"))))
        (m/depends-on ["push-manifest"])
        (m/blocked))))

(defn notify [ctx]
  (when (release? ctx)
    (p/pushover-msg {:msg (str "Website version " (m/tag ctx) " has been published.")
                     :dependencies ["push-manifest"]})))

[test-common
 deploy-common
 test-site
 test-docs
 build-site
 build-docs
 image
 deploy
 notify]
