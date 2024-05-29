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
   {:save-artifacts [{:id "site"
                      :path "target"}]}))

(def img-base "fra.ocir.io/frjdhmocn5qi")

(def build-image? (some-fn bc/main-branch? bc/tag))

(defn image
  "Generates the container image"
  [ctx]
  (when (build-image? ctx)
    (let [creds (get (api/build-params ctx) "dockerhub-creds")
          config-file "/tmp/docker-config.json"
          version (or (bc/tag ctx) (get-in ctx [:build :build-id]))
          img (str img-base "/website:" version)]
      (bc/container-job
       "image"
       {:image "docker.io/monkeyci/kaniko:1.21.0"
        :container/env {"DOCKER_CREDS" creds
                        "DOCKER_CONFIG" config-file}
        :script [(str "echo $DOCKER_CREDS > " config-file)
                 (str "/kaniko/executor --destination " img)]
        :dependencies ["build"]
        :restore-artifacts [{:id "site"
                             :path "site/target"}]}))))

[build
 image]
