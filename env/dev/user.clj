(ns user
  (:require [monkey.ci.site.core :as c]))

(defn build-site
  "Builds the site to target directory"
  []
  (c/build {:output "target"
            :config {:base-url "staging.monkeyci.com"
                     :api-url "http://localhost:3000"}}))
