(ns monkey.ci.template.build
  (:require [aero.core :as ac]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [hiccup2.core :as h]))

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
  [output f]
  (println "Generating HTML to" (str output))
  (fs/create-dirs output)
  (generate-file f "index.html" output))

(defn- copy-tree [src dest]
  (fs/copy-tree src dest {:replace-existing true}))

(defn copy-assets
  "Copies asset files from the assets dir to destination"
  [dest]
  (println "Copying assets to" (str dest))
  (copy-tree "assets" dest)
  (copy-tree "../assets" dest))

(defn load-config
  "If `config` is an existing file, loads it using aero, otherwise just returns it."
  [config]
  (if (string? config)
    (when (fs/exists? config)
      (ac/read-config config))
    config))

(defn- resolve-fn [sym]
  (use (symbol (namespace sym)))
  (resolve sym))

(defn build
  "Builds the entire site by generating html and copying assets."
  [{:keys [output site-fn config]}]
  (if-let [f (resolve-fn site-fn)]
    (do
      (copy-assets output)
      (generate output #(f (load-config config))))
    (throw (ex-info "Could not resolve site fn" {:site-fn site-fn}))))
