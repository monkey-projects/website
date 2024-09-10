(ns monkey.ci.site.build
  (:require [monkey.ci.build
             [api :as api]
             [core :as bc]
             [shell :as s]]
            [monkey.ci.plugin
             [infra :as infra]
             [kaniko :as kaniko]]))

(defn get-env
  "Determines deployment environment from the build info"
  [ctx]
  (if (bc/tag ctx) :prod :staging))

(def config-by-env
  {:prod
   {:base-url "monkeyci.com"}
   :staging
   {:base-url "staging.monkeyci.com"}})

(defn build
  "Builds the website and docs files"
  [id ctx]
  (bc/action-job
   (str "build-" id)
   (s/bash (format "clojure -M:%s/build '%s'" id (pr-str {:config (get config-by-env (get-env ctx))})))
   {:save-artifacts [{:id id
                      :path (str id "/target")}]}))

(def build-site (partial build "site"))
(def build-docs (partial build "docs"))

(def img-base "fra.ocir.io/frjdhmocn5qi/website")

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
                                   {:id "docs"
                                    :path "docs/target"}]))))

(def deploy
  (bc/action-job
   "deploy"
   (fn [ctx]
     (if-let [token (get (api/build-params ctx) "github-token")]
       ;; Patch the kustomization file
       (if (infra/patch+commit! (infra/make-client token)
                                (get-env ctx)
                                "website"
                                (img-version ctx))
         bc/success
         (assoc bc/failure :message "Unable to patch version in infra repo"))
       (assoc bc/failure :message "No github token provided")))
   {:dependencies ["image"]}))

[build-site
 build-docs
 image
 deploy]
