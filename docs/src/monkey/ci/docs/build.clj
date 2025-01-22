(ns monkey.ci.docs.build
  "Functions for building the site output files"
  (:require [babashka.fs :as fs]
            [clojure.tools.logging :as log]
            [monkey.ci.docs
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

(defn location
  "Calculates location vector for breadcrumb"
  [md p conf]
  ;; TODO Allow for multiple levels using category
  (when-not (:home? md)
    [{:path (-> (fs/relativize (get-input conf) p)
                (fs/strip-ext)
                (str "/"))
      :label (m/short-title md)}]))

(defn- list-tree
  "Walks the file tree, returns a list of all markdown file paths"
  [dir]
  (let [{files false subdirs true} (->> (fs/list-dir dir)
                                        (group-by fs/directory?))]
    (->> files
         (filter markdown-file?)
         (concat (mapcat list-tree subdirs)))))

(defn- build-toc
  "Generates table of contents according to the given file list"
  [files]
  (->> files
       (map (fn [{:keys [md]}]
              ;; Use location
              (let [loc (-> md :location last)]
                {:title (m/short-title md)
                 :path (or (:path loc) "/")})))))

(defn- build-dir
  "Traverses the given directory tree and recursively generates pages from 
   each encountered markdown file."
  [config dir]
  (let [add-location (fn [md f]
                       (assoc md :location (location md f config)))
        files (->> (list-tree dir)
                   (map (fn [f]
                          (-> {:file f
                               :md (-> (md/parse f (:config config)))}
                              (update :md add-location f)))))
        toc (build-toc files)]
    (letfn [(gen-file [{:keys [file md]}]
              (log/debug "Generating output for" file)
              (try
                (let [md (add-location md file)
                      html (m/md->page md (-> config
                                              :config
                                              (assoc :toc toc)))
                      out (output-path md file config)]
                  (tb/write-html html out)
                  out)
                (catch Exception ex
                  (log/warn "Failed to process file" file ":" (ex-message ex)))))]
      (->> files
           (map gen-file)
           (doall)))))

(defn build-all [config]
  (let [src (get-input config)
        res (build-dir config src)]
    ;; Only copy local assets, the others are pulled from the assets server
    (tb/copy-tree "assets" (:output config))
    res))
