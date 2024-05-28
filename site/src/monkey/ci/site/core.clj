(ns monkey.ci.site.core
  (:require [clojure.java.io :as io]
            [hiccup2.core :as h]
            [monkey.ci.site.main :as main]))

(defn create-parent-dir! [path]
  (.. (io/file path) (getParentFile) (mkdirs)))

(defn generate [{:keys [output]}]
  (println "Generating HTML to" output)
  (create-parent-dir! output)
  (->> (main/main)
       (h/html)
       (str)
       (spit output)))
