(ns monkey.ci.template.svg
  "Functions to include svg images or icons in pages"
  (:require [babashka.fs :as fs]
            [hickory.core :as h]))

(defn include
  "Loads the svg file from the configured `svg-path` combining the name with
   an `svg` extension.  The file is read and parsed into a hiccup structure."
  [conf n]
  (->> (fs/file (fs/path (:svg-path conf) (str n ".svg")))
       (slurp)
       (h/parse-fragment)
       (map h/as-hiccup)
       (first)))
