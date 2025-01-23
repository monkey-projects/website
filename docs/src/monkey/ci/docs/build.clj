(ns monkey.ci.docs.build
  "Functions for building the site output files"
  (:require [babashka.fs :as fs]
            [clojure.tools.logging :as log]
            [monkey.ci.docs
             [config :as dc]
             [main :as m]
             [md :as md]]
            [monkey.ci.template.build :as tb]))

(def default-content-dir "content")
(def idx-file "index.html")

(defn- get-input [config]
  (get config :input default-content-dir))

(defn- markdown-file? [x]
  (and (not (fs/directory? x))
       (= "md" (fs/extension x))))

(defn output-path
  "Calculates output path using the parsed markdown and input path"
  [md in {:keys [output] :as conf}]
  (if (:home? md)
    (fs/path output idx-file)
    (-> (fs/strip-ext in)
        (as-> p (fs/relativize (get-input conf) p))
        (fs/path idx-file)
        (as-> p (fs/path output p)))))

(def home-location
  {:path "/"
   :label "Home"})

(defn location
  "Calculates location vector for breadcrumb"
  [md p conf]
  ;; TODO Allow for multiple levels using category
  (cond-> [home-location]
    (not (:home? md))
    (conj {:path (-> (fs/relativize (get-input conf) p)
                     (fs/strip-ext)
                     (str "/")
                     (dc/apply-prefix (dc/articles-prefix conf)))
           :label (m/short-title md)})))

(defn- list-tree
  "Walks the file tree, returns a list of all markdown file paths"
  [dir]
  (let [{files false subdirs true} (->> (fs/list-dir dir)
                                        (group-by fs/directory?))]
    (->> files
         (filter markdown-file?)
         (concat (mapcat list-tree subdirs)))))

(defn- parse-files
  "Recursively lists and parses all markdown files in given directory"
  [config]
  (letfn [(add-location [md f]
            (assoc md :location (location md f config)))]
    (->> (list-tree (get-input config))
         (map (fn [f]
                (-> {:file f
                     :md (-> (md/parse f (:config config)))}
                    (update :md add-location f)))))))

(defn- build-toc
  "Generates table of contents according to the given file list"
  [files]
  ;; TODO Add ordering and grouping according to categories
  (->> files
       (map (fn [{:keys [md]}]
              ;; Use location
              (let [loc (-> md :location last)]
                {:title (m/short-title md)
                 :path (or (:path loc) "/")})))))

(defn- gen-file [config {:keys [file md]}]
  (log/debug "Generating output for" file)
  (try
    (let [html (m/md->page md (:config config))
          out (output-path md file config)]
      (tb/write-html html out)
      out)
    (catch Exception ex
      (log/warn "Failed to process file" file ":" (ex-message ex)))))

(defn- build-dir
  "Traverses the given directory tree and recursively generates pages from 
   each encountered markdown file."
  [{:keys [files] :as config}]
  (->> files
       (map (partial gen-file config))
       (doall)))

(defn- set-dir [config dir]
  (update config :output fs/path dir))

(defn- gen-index
  "Generates index file, by finding the input file marked with `home?`"
  [{:keys [files] :as config}]
  (let [idx (->> files
                 (filter (comp :home? :md))
                 (first))]
    (assoc config :index (gen-file config idx))))

(defn- for-articles [config]
  (set-dir config "articles/"))

(defn- gen-articles
  "Generates all article files.  Each of the files in the content directory are
   rendered as an article, mirroring the directory structure.  Only the file marked
   as `home?` is skipped, since it's used to build the base index page."
  [config]
  (->> (build-dir (for-articles config))
       (assoc config :articles)))

(defn- gen-categories
  "Generates categories files.  Each of the categories encountered when processing
   the articles is added here.  They are sorted according to the `cat-idx` property.
   The category page itself is composed of the summaries of each article document,
   or failing that, the first paragraph."
  [{:keys [files] :as config}]
  ;; TODO
  config)

(defn- copy-assets [config]
  ;; Only copy local assets, the others are pulled from the assets server
  (tb/copy-tree "assets" (:output config))
  config)

(defn build-all [config]
  (-> (assoc config :files (parse-files (for-articles config)))
      (gen-index)
      (gen-categories)
      (gen-articles)
      (copy-assets)))
