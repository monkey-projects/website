(ns monkey.ci.docs.md
  "Markdown processing"
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nextjournal.markdown :as md]
            [nextjournal.markdown.transform :as mdt]))

(defprotocol InputSource
  (->reader [x]))

(extend-type java.lang.String
  InputSource
  (->reader [s]
    (java.io.StringReader. s)))

(extend-type java.nio.file.Path
  InputSource
  (->reader [p]
    (io/reader (fs/file p))))

(extend-type java.io.Reader
  InputSource
  (->reader [r]
    r))

(defn- transform-heading [ctx {:keys [attrs] :as node}]
  (-> node
      ;; Transform all h1 headers into h4
      (update :heading-level + 3)
      (mdt/heading-markup)
      (conj attrs)
      (mdt/into-markup ctx node)))

(defn- transform-code [ctx {:keys [info] :as node}]
  [:pre {:class (cond-> "viewer-code not-prose mb-2"
                  info (str " language-" info))}
   (mdt/into-markup [:code] ctx node)])

(defn parse
  "Parses the given markdown content and returns it as a hiccup style structure.
   Any leading edn structure is added to the metadata."
  [content]
  (with-open [b (java.io.BufferedReader. (->reader content))
              r (java.io.PushbackReader. b)]
    (let [meta (try
                 (.mark b 1000) ; Support up to 1k buffer for invalid edn
                 (edn/read r)
                 (catch Exception ex
                   (if (.startsWith (ex-message ex) "No dispatch macro")
                     (.reset b)        ; No edn at start, so ignore it
                     (throw ex))))
          s (slurp b)]
      (assoc meta
             :contents (->> s
                            (md/parse)
                            (mdt/->hiccup
                             (assoc mdt/default-hiccup-renderers
                                    :heading transform-heading
                                    :plain (partial mdt/into-markup [:span])
                                    :code transform-code)))))))
