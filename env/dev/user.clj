(ns user
  (:require [monkey.ci.site.core :as sc]
            [monkey.ci.docs.core :as dc]))

(defn build-site
  "Builds the site to target directory"
  []
  (sc/build {:output "site/target"
             :config {:base-url "staging.monkeyci.com"
                      :api-url "http://localhost:3000"}}))

(defn build-docs
  "Builds the docs to target directory"
  []
  (dc/build {:output "docs/target"
             :config {:base-url "staging.monkeyci.com"
                      :api-url "http://localhost:3000"}}))
