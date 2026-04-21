(ns build
  (:require [cheshire.core :as json]
            [monkey.ci.api :as m]))

(defn read-json [ctx]
  (let [v (-> (slurp "file.json")
              (json/parse-string)
              (get-in ["result" "exit"]))]
    (when (not= 0 v)
      (-> m/failure
          (m/with-message "Exit code in json is non-zero")))))

(def custom-action
  "Executes a custom code job"
  (m/action-job "custom-action" read-json))
