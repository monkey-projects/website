(ns monkey.ci.site.core
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [hiccup2.core :as h]
            [monkey.ci.site.main :as main]))

(defn create-parent-dir! [path]
  (.. (io/file path) (getParentFile) (mkdirs)))

(defn- generate-file
  "Generates hiccup using `h`, and writes the result to file `f` in directory `dir`."
  [h f dir]
  (->> (h)
       (h/html)
       (str)
       (spit (fs/file (fs/path dir f)))))

(defn generate
  "Generates HTML from the hiccup code"
  [{:keys [output]}]
  (println "Generating HTML to" output)
  (fs/create-dirs output)
  (generate-file main/main "index.html" output))

(defn copy-assets
  "Copies asset files from the assets dir to destination"
  [dest]
  (println "Copying assets")
  (fs/copy-tree "../assets" dest {:replace-existing true})
  (fs/copy-tree "assets" dest {:replace-existing true}))

(defn build
  "Builds the entire site by generating html and copying assets."
  [{:keys [output] :as args}]
  (copy-assets output)
  (generate args))
