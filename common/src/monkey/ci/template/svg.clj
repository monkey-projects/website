(ns monkey.ci.template.svg
  "Functions to include svg images or icons in pages"
  (:require [babashka.fs :as fs]))

(defn include [conf n]
  (slurp (fs/file (fs/path (:svg-path conf) (str n ".svg")))))
