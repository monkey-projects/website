(ns monkey.ci.docs.build
  "Functions for building the site output files"
  (:require [aero.core :as ac]
            [babashka.fs :as fs]
            [clojure.tools.logging :as log]
            [medley.core :as mc]
            [monkey.ci.docs
             [config :as dc]
             [main :as m]
             [md :as md]]
            [monkey.ci.template.build :as tb]))

(def idx-file "index.html")

(defn- markdown-file? [x]
  (and (not (fs/directory? x))
       (= "md" (fs/extension x))))

(defn output-path [in {:keys [output] :as conf}]
  (-> (fs/strip-ext in)
      (as-> p (fs/relativize (dc/get-input conf) p))
      (fs/path idx-file)
      (as-> p (fs/path output p))))

(def home-location
  {:path "/"
   :label "Home"})

(defn- category-location [id category conf]
  (when category
    {:path (-> id
               name
               (dc/apply-prefix (dc/categories-prefix conf)))
     :label (:label category)}))

(defn location
  "Calculates location vector for breadcrumb"
  [{:keys [category] :as md} p conf]
  (cond-> [home-location]
    category
    (conj (or (category-location category
                                 (get-in conf [:categories category])
                                 conf)
              (throw (ex-info "Category not found" {:category category
                                                    :file p
                                                    :config conf}))))
    
    (not (:home? md))
    (conj {:path (-> (fs/relativize (dc/get-input conf) p)
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
    (->> (list-tree (dc/get-input config))
         (map (fn [f]
                (try
                  (-> {:file f
                       :md (-> (md/parse f (:config config)))}
                      (update :md add-location f))
                  (catch Exception ex
                    (log/warn "Failed to process file" f ":" (ex-message ex))))))
         (remove nil?)
         (assoc config :files))))

(defn- gen-file [html out]
  (tb/write-html html out)
  out)

(defn- article-file [config {:keys [file md]}]
  (log/debug "Generating output for" file)
  (let [html (m/md->page md (:config config))
        out (output-path file config)]
    (gen-file html out)))

(defn- set-dir [config dir]
  (update config :output fs/path dir))

(defn- gen-index
  "Generates index file, by finding the input file marked with `home?`"
  [{:keys [files output] :as config}]
  (let [idx (->> files
                 (filter (comp :home? :md))
                 (first)
                 :md)]
    (assoc config :index (gen-file (m/index-page idx config)
                                   (fs/path output idx-file)))))

(defn- for-articles [config]
  (set-dir config "articles/"))

(defn- for-categories [config]
  (set-dir config "categories/"))

(defn- gen-articles
  "Generates all article files.  Each of the files in the content directory are
   rendered as an article, mirroring the directory structure.  Only the file marked
   as `home?` is skipped, since it's used to build the base index page."
  [{:keys [files] :as config}]
  (->> files
       (map (partial article-file (for-articles config)))
       (doall)
       (assoc config :articles)))

(defn configure-categories
  "Uses the information extracted from parsed markdown files, combined with configuration
   to build a categories map."
  [{:keys [files categories] :as config}]
  (->> (reduce (fn [res f]
                 (let [c (get-in f [:md :category])]
                   (cond-> res
                     c (update-in [c :files] (fnil conj []) f))))
               categories
               files)
       (mc/map-kv-vals
        (fn [id c]
          (assoc c :location [home-location
                              (category-location id c config)])))))

(defn- add-categories [config]
  (assoc config :categories (configure-categories config)))

(defn- gen-categories
  "Generates categories files.  Each of the categories encountered when processing
   the articles is added here.  They are sorted according to the `cat-idx` property.
   The category page itself is composed of the summaries of each article document,
   or failing that, the first paragraph."
  [config]
  (let [categories (:categories config)]
    (log/debugf "Generating %d category pages" (count categories))
    (->> categories
         (map (fn [[id cat]]
                (let [p (fs/path (:output config) "categories" (name id) idx-file)]
                  (tb/write-html (m/category-page id config) p)
                  (-> cat
                      (assoc :file p)
                      (update :files count)))))
         (doall)
         (assoc config :categories))))

(defn- copy-assets [config]
  ;; Only copy local assets, the others are pulled from the assets server
  (tb/copy-tree "assets" (:output config))
  config)

(defn- load-config [config]
  (let [p (fs/path (dc/get-input config) "config.edn")]
    (when (fs/exists? p)
      (ac/read-config (fs/file p)))))

(defn build-all [config]
  (->> config
       (merge (load-config config))
       (parse-files)
       (add-categories)
       (gen-index)
       (gen-categories)
       (gen-articles)
       (copy-assets)))
