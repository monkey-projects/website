(ns user
  (:require [monkey.ci.docs.build :as db]))

(defn build-docs
  "Builds the docs to target directory"
  []
  (db/build-all {:output "target/site"
                 :config {:base-url "staging.monkeyci.com"
                          :api-url "http://localhost:3000"
                          :path-prefix "/"}}))
