(ns monkey.ci.docs.md
  "Markdown processing"
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [hiccup2.core :as hiccup]
            [monkey.ci.docs
             [config :as dc]
             [input :as i]]
            [nextjournal.markdown :as md]
            [nextjournal.markdown.transform :as mdt])
  (:import [java.io BufferedReader PushbackReader Reader]))

(defn- transform-heading [ctx {:keys [attrs] :as node}]
  (letfn [(heading-markup [{l :heading-level}] [(keyword (str "h" (or l 1))) attrs])]
    (-> node
        ;; Transform all h1 headers into h4
        (update :heading-level + 3)
        (heading-markup)
        (mdt/into-markup ctx node))))

(defn- transform-code [ctx {:keys [info] :as node}]
  [:pre {:class (cond-> "viewer-code not-prose mb-2"
                  info (str " language-" info))}
   (mdt/into-markup [:code] ctx node)])

(defn- relative? [x]
  (nil? (re-matches #"^(http://|https://|/).*$" x)))

(defn- transform-link
  "Prepends any configured path prefix to relative paths"
  [conf ctx {:keys [attrs] :as node}]
  (letfn [(convert-path [{:keys [href] :as a}]
            (let [rel? (relative? href)]
              (cond-> a
                rel? (update :href (partial str (dc/articles-prefix conf)))
                (not rel?) (assoc :target :_blank))))]
    (mdt/into-markup [:a (convert-path attrs)] ctx node)))

(def transform-quote (partial mdt/into-markup [:blockquote.blockquote.blockquote-sm.mb-2]))

(defn- transform-img [ctx node]
  (let [orig (:image mdt/default-hiccup-renderers)]
    (->> (orig ctx node)
         (vector :div.shadow.text-center))))

(defn read-header
  "Given a reader, tries to read the leading metadata edn structure.  Input should be 
   a `java.io.BufferedReader`"
  [^BufferedReader b]
  (let [r (PushbackReader. b)]
    (try
      (.mark b 1000)         ; Support up to 1k buffer for invalid edn
      (edn/read r)
      (catch Exception ex
        (if (.startsWith (ex-message ex) "No dispatch macro")
          (.reset b)                   ; No edn at start, so ignore it
          (throw ex))))))

(defn- hiccup-renderers [opts]
  (assoc mdt/default-hiccup-renderers
         :heading transform-heading
         :plain (partial mdt/into-markup [:span])
         :code transform-code
         :link (partial transform-link opts)
         :blockquote transform-quote
         :table (partial mdt/into-markup [:table.table])
         :image transform-img
         :html-inline (comp hiccup/raw md/node->text)
         :html-block (comp hiccup/raw md/node->text)))

(defn ^BufferedReader buffered [^Reader r]
  (BufferedReader. r))

(defn parse-raw
  "Parses raw markdown, i.e. without header.  Returns a hiccup structure."
  [s & [opts]]
  (->> s
       (md/parse)
       (mdt/->hiccup (hiccup-renderers opts))))

(defn parse
  "Parses the given markdown content and returns it as a hiccup style structure.
   Any leading edn structure is added to the metadata.  Extra options can be
   specified for transformations."
  [content & [opts]]
  (with-open [b (buffered (i/->reader content))]
    (let [meta (read-header b)
          s (slurp b)]
      (->> (parse-raw s opts)
           (assoc meta :contents)))))
