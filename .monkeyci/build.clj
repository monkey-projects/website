(ns monkey.ci.site.build
  (:require [monkey.ci.build
             [api :as api]
             [core :as bc]
             [shell :as s]]))

(def build
  "Builds the website files"
  (bc/action-job
   "build"
   (s/bash "clojure -X:build")
   {:work-dir "site"
    :save-artifacts [{:id "site"
                      :path "target"}]}))

(def img-base "fra.ocir.io/frjdhmocn5qi/website")

(def build-image? (some-fn bc/main-branch? bc/tag))

(defn image
  "Generates the container image"
  [ctx]
  (when (build-image? ctx)
    (let [wd (s/container-work-dir ctx)
          creds (get (api/build-params ctx) "dockerhub-creds")
          config-dir "/kaniko/.docker"
          config-file (str config-dir "/config.json")
          version (or (bc/tag ctx) (get-in ctx [:build :build-id]))
          img (str img-base ":" version)]
      (bc/container-job
       "image"
       {:image "docker.io/monkeyci/kaniko:1.21.0"
        :container/env {"DOCKER_CREDS" creds
                        ;; Must point to the directory where 'config.json' is in
                        "DOCKER_CONFIG" config-dir}
        ;; Kaniko requires that docker credentials are written to file
        :script [(str "echo $DOCKER_CREDS > " config-file)
                 (format "/kaniko/executor --destination %s --dockerfile %s --context dir://%s"
                         img (str wd "/Dockerfile") wd)]
        :dependencies ["build"]
        :restore-artifacts [{:id "site"
                             :path "site/target"}]}))))

[build
 image]
