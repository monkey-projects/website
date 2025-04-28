(ns monkey.ci.site.core
  (:require [monkey.ci.site.main :as m]
            [monkey.ci.template
             [build :as tb]
             [components :as tc]]))

(def site-pages
  {"index" m/main
   "error-404" tc/not-found-page})

(defn build [opts]
  (tb/site (assoc opts :pages site-pages)))
