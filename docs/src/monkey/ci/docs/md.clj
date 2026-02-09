(ns monkey.ci.docs.md
  "Markdown processing"
  (:require [clojure.edn :as edn]
            [monkey.ci.docs
             [config :as dc]
             [input :as i]]
            [monkey.ci.template.md :as tmd]
            [nextjournal.markdown.transform :as mdt])
  (:import (java.io BufferedReader PushbackReader Reader)))

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

(defn- hiccup-renderers [opts]
  (assoc tmd/hiccup-renderers
         :link (partial transform-link opts)))

(defn parse-raw
  "Parses raw markdown, i.e. without header.  Returns a hiccup structure."
  [s & [opts]]
  (tmd/parse-raw s (hiccup-renderers opts)))

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

(defn ^BufferedReader buffered [^Reader r]
  (BufferedReader. r))

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
