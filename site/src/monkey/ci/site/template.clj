(ns monkey.ci.site.template
  "Common template functions"
  (:require [hiccup2.core :as h]
            [monkey.ci.template.components :as cc]))

(defn head [config]
  (conj (cc/head config)
        ;; Used in scripts
        [:script (h/raw (format "var apiUrl='%s';" (cc/api-url config)))]))

(def header cc/header-dark)

(defn footer [config]
  [(cc/footer config)
   (cc/script (cc/script-url config "bootstrap.min.js"))
   (cc/script (cc/script-url config "theme.min.js"))
   (cc/script (cc/site-url config "/js/site.js"))])

(def shape-1
  [:div.shape-container
   [:div.shape.shape-bottom.zi-1
    [:svg {:viewbox "0 0 3000 600"
           :fill "none"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path {:d "M0 600V350.234L3000 0V600H0Z"
             :fill "#fff"}]]]])

(defn wrap-template
  "Given raw contents, as parsed from markdown, wraps it in template structure to create 
   a full html document."
  [conf & contents]
  [:html
   (head conf)
   (vec
    (concat
     (into [:body] contents)
     (footer conf)))])
