(ns user
  (:require [monkey.ci.template.build :as tb]))

(defn build-docs
  "Builds the docs to target directory"
  []
  (tb/build {:output "target"
             :site-fn 'monkey.ci.docs.main/main
             :config {:base-url "staging.monkeyci.com"
                      :api-url "http://localhost:3000"}}))
