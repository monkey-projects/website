(ns monkey.ci.docs.build
  "Functions for building the site output files"
  (:require [babashka.fs :as fs]
            [clojure.tools.logging :as log]
            [monkey.ci.docs
             [main :as m]
             [md :as md]]
            [monkey.ci.template.build :as tb]))

(def default-content-dir "content/md")

(defn- get-input [config]
  (get config :input default-content-dir))

(defn- markdown-file? [x]
  (and (not (fs/directory? x))
       (= "md" (fs/extension x))))

(defn- output-path
  "Calculates output path using the parsed markdown and input path"
  [md in {:keys [output] :as conf}]
  (if (:home? md)
    (fs/path output "index.html")
    (-> (fs/strip-ext in)
        (as-> p (fs/relativize (get-input conf) p))
        (fs/path "index.html")
        (as-> p (fs/path output p)))))

(defn- build-dir [config dir]
  (let [{files false subdirs true} (->> (fs/list-dir dir)
                                        (group-by fs/directory?))
        gen-file (fn [f]
                   (log/debug "Generating output for" f)
                   (let [md (md/parse f)
                         html (m/md->page md config)
                         out (output-path md f config)]
                     (tb/write-html html out)
                     out))
        gen-subs (fn []
                   (mapcat (partial build-dir config) subdirs))]
    (concat (->> files
                 (filter markdown-file?)
                 (map gen-file)
                 (doall))
            (gen-subs))))

(defn build-all [config]
  (let [src (get-input config)]
    (build-dir config src)))
