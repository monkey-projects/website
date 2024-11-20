(ns build
  (:require [monkey.ci.build
             [api :as api]
             [core :as bc]
             [shell :as s]]
            [monkey.ci.plugin
             [clj :as clj]
             [infra :as infra]
             [kaniko :as kaniko]
             [pushover :as p]]))

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

(def common-changed?
  "True if any of the files in the `common/` dir have changed"
  (touched? #"^common/.*"))

(defn- clj-cmd
  "Runs a local clojure command, without starting a container"
  [job-id cache-id cmd & [opts]]
  (let [m2 (str ".m2-" cache-id)]
    (bc/action-job
     job-id
     (s/bash (format "clojure -Sdeps '{:mvn/local-repo \"%s\"}' %s" m2 cmd))
     (assoc opts :caches [{:id (str "mvn-" cache-id)
                           :path m2}]))))

(defn run-tests
  "Runs tests for given site"
  [id & [alias]]
  (fn [_]
    (-> (clj-cmd
         (str "test-" id)
         id
         (format "-X%s:test:junit" (or (some-> alias str) "")))
        (assoc :work-dir id))))

(defn test-common [ctx]
  (when (common-changed? ctx)
    ((run-tests "common") ctx)))

(def test-site (run-tests "site"))
(def test-docs (run-tests "docs" :template))

(defn deploy-common [ctx]
  (when (common-changed? ctx)
    (-> ((clj/deps-publish {:publish-job-id "deploy-common"
                            :test-job-id "test-common"}) ctx)
        (assoc :work-dir "common"))))

(defn build
  "Builds the website and docs files"
  [id alias ctx]
  (clj-cmd
   (str "build-" id)
   id
   (format "-X%s '%s'" (str alias) (pr-str {:config (get config-by-env (get-env ctx))}))
   {:save-artifacts [{:id id
                      :path (str id "/target")}]
    :dependencies (cond-> [(str "test-" id)]
                    (common-changed? ctx) (conj "deploy-common"))
    :work-dir id}))

(def build-site (partial build "site" :build))
(def build-docs-theme (partial build "docs" :template))

(def build-docs-site
  (clj-cmd
   "build-docs-site"
   "docs-site"
   "-M:cryogen:build"
   {:work-dir "docs"
    :save-artifacts [{:id "docs-contents"
                      :path "public"}]}))

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
        (assoc :dependencies ["build-site" "build-docs"]
               :restore-artifacts [{:id "site"
                                    :path "site/target"}
                                   {:id "docs-contents"
                                    :path "docs-contents/public"}]))))

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
