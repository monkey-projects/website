(ns monkey.ci.site.core
  (:require [monkey.ci.site
             [about :as a]
             [main :as m]
             [md :as md]]
            [monkey.ci.template
             [build :as tb]
             [components :as tc]]))

(def site-pages
  {"index" m/main
   "about/index" a/about
   "error-404" tc/not-found-page
   "privacy-policy/index" (md/md-fn "md/privacy-policy.md")
   "terms-of-use/index" (md/md-fn "md/terms-of-use.md")})

(defn build [opts]
  (tb/site (assoc opts :pages site-pages)))
