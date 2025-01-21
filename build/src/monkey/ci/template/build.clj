(ns monkey.ci.template.build
  (:require [aero.core :as ac]
            [babashka.fs :as fs]
            [hiccup2.core :as h]))

(defn write-html
  "Given a hiccup structure, converts it to html and writes the result to the given path"
  [in p]
  (fs/create-dirs (fs/parent p))
  (->> in
       (h/html)
       (str)
       (spit (fs/file p))))

(defn- generate-file
  "Generates hiccup using `h`, and writes the result to file `f` in directory `dir`."
  [h f]
  (write-html (h) f))

(defn generate
  "Generates HTML from the hiccup code"
  [output f]
  (let [output (cond-> output
                 (not= "html" (fs/extension output)) (fs/path "index.html"))]
    (println "Generating HTML to" (str output))
    (generate-file f output)
    output))

(defn copy-tree [src dest]
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
  (cond
    (symbol? sym)
    (do
      (use (symbol (namespace sym)))
      (resolve sym))
    (fn? sym)
    sym))

(defn page
  "Generates a single page, does not copy assets."
  [{:keys [output site-fn config]}]
  (println "Building:" site-fn)
  (if-let [f (resolve-fn site-fn)]
    (generate output #(f (load-config config)))
    (throw (ex-info "Could not resolve site fn" {:site-fn site-fn}))))

(defn site
  "Builds the entire site by generating html and copying assets."
  [{:keys [output] :as opts}]
  (copy-assets output)
  (page opts))

(def build site)
