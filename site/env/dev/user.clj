(ns user
  (:require [monkey.ci.site.core :as c]))

(defn build
  "Builds the site to target directory"
  []
  (c/build {:output "target"}))
