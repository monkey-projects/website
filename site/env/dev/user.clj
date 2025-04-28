(ns user
  (:require [monkey.ci.template.build :as tb]
            [monkey.ci.site.main :as sm]
            [monkey.ci.template.components :as tc]))

(defn build-site
  "Builds the site to target directory"
  []
  (tb/site {:output "target"
            :pages {"index" sm/main
                    "error-404" tc/not-found-page}
            :config {:base-url "staging.monkeyci.com"
                     :api-url "http://localhost:3000"}}))
