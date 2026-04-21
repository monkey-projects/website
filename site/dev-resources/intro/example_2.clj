(ns build
  (:require [monkey.ci.api :as m]))

(defn deploy
  "Deploys only on main branch"
  [ctx]
  (when (m/main-branch? ctx)
    (-> (m/container-job "deploy")
        (m/image "docker.io/gradle:latest")
        (m/script ["gradle publish"]))))
