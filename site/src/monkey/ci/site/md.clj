(ns monkey.ci.site.md
  (:require [clojure.java.io :as io]
            [clojure.walk :as cw]
            [monkey.ci.site.template :as t]
            [nextjournal.markdown :as md]
            [nextjournal.markdown.transform :as mdt]))

(defn strip-<> [v]
  (cw/prewalk
   (fn [x]
     (if (and (vector? x) (= :<> (first (second x))))
       (into [(first x)]
             (rest (second x)))
       x))
   v))

(defn- transform-li [ctx node]
  (strip-<> (mdt/into-markup [:li] ctx node)))

(def renderers
  (assoc mdt/default-hiccup-renderers
         :list-item transform-li))

(defn md-page
  "Converts markdown contents in to a hiccup page"
  [contents]
  (->> (md/parse contents)
       (mdt/->hiccup renderers)))

(defn md-resource [f]
  (-> (io/resource f)
      (slurp)
      (md-page)))

(defn md-fn [f]
  (fn [conf]
    (t/wrap-template
     conf
     (t/header conf)
     [:main#content
      [:div.bg-primary-dark
       [:div.container.position-relative.zi-2.content-space-b-1.content-space-t-2.content-space-md-3
        [:div.bg-white.p-3
         (md-resource f)]]]])))
