(ns monkey.ci.site.md
  (:require [clojure.java.io :as io]
            [nextjournal.markdown :as md]
            [nextjournal.markdown.transform :as mdt]))

(defn md-page [contents]
  (->> (md/parse contents)
       (mdt/->hiccup)))

(defn md-resource [f]
  (-> (io/resource f)
      (slurp)
      (md-page)))

(defn md-fn [f]
  (constantly (md-resource f)))
