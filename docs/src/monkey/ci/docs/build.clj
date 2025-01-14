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
  ;; TODO Allow for multiple levels
  (when-not (:home? md)
    [{:path (-> (fs/relativize (get-input conf) p)
                (fs/strip-ext)
                (str "/"))
      :label (:title md)}]))

(defn- build-dir
  "Traverses the given directory tree and recursively generates pages from 
   each encountered markdown file."
  [config dir]
  (let [{files false subdirs true} (->> (fs/list-dir dir)
                                        (group-by fs/directory?))]
    (letfn [(add-location [md f]
              (assoc md :location (location md f config)))
            (gen-file [f]
              (log/debug "Generating output for" f)
              (let [md (-> (md/parse f)
                           (add-location f))
                    html (m/md->page md config)
                    out (output-path md f config)]
                (tb/write-html html out)
                out))
            (gen-subs []
              (mapcat (partial build-dir config) subdirs))]
      (concat (->> files
                   (filter markdown-file?)
                   (map gen-file)
                   (doall))
              (gen-subs)))))

(defn build-all [config]
  (let [src (get-input config)]
    (build-dir config src)))
