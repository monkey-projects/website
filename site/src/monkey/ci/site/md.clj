(ns monkey.ci.site.md
  (:require [clojure.java.io :as io]
            [clojure.walk :as cw]
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

(defn md-page [contents]
  (->> (md/parse contents)
       (mdt/->hiccup renderers)))

(defn md-resource [f]
  (-> (io/resource f)
      (slurp)
      (md-page)))

(defn md-fn [f]
  (constantly (md-resource f)))
