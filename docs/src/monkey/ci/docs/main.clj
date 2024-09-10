(ns monkey.ci.docs.main
  (:require [monkey.ci.common.components :as cc]))

(def content
  [:main#content {:role "main"}
   [:div.overflow-hidden
    [:div.bg-primary-dark
     [:div.container.position-relative.zi-2.content-space-b-1.content-space-t-2.content-space-md-3
      [:h1 "TODO"]]]]])

(defn main [config]
  [:html
   cc/head
   [:body
    (cc/header config)
    content
    cc/footer
    (cc/script "vendor.min.js")
    (cc/script "theme.min.js")
    (cc/script "site.js")]])
