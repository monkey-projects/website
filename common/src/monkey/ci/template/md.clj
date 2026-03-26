(ns monkey.ci.template.md
  "Markdown processing"
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [hiccup2.core :as hiccup]
            [nextjournal.markdown :as md]
            [monkey.ci.template.input :as i])
  (:import [java.io BufferedReader PushbackReader Reader]))

(defn- transform-heading [ctx {:keys [attrs] :as node}]
  (letfn [(heading-markup [{l :heading-level}] [(keyword (str "h" (or l 1))) attrs])]
    (-> node
        ;; Transform all h1 headers into h4
        (update :heading-level + 3)
        (heading-markup)
        (md/into-hiccup ctx node))))

(defn- copy-btn [id]
  [:button.btn.btn-sm.copy-btn
   {:data-clipboard-target (str "#" id)}
   "Copy"])

(defn- transform-code [ctx {:keys [info] :as node}]
  ;; Assign a random id so the clipboard lib knows what to copy
  (let [id (str (random-uuid))]
    [:div
     [:small info]
     [:pre {:class (cond-> "viewer-code not-prose mb-2"
                     info (str " language-" info))}
      [:div
       (md/into-hiccup [:code {:id id}] ctx node)
       (copy-btn id)]]]))

(defn- relative? [x]
  (nil? (re-matches #"^(http://|https://|/).*$" x)))

(def transform-quote (partial md/into-hiccup [:blockquote.blockquote.blockquote-sm.mb-2]))

(defn- transform-img [ctx node]
  (let [orig (:image md/default-hiccup-renderers)]
    (->> (orig ctx node)
         (vector :div.shadow.text-center))))

(def hiccup-renderers
  (assoc md/default-hiccup-renderers
         :heading transform-heading
         :plain (partial md/into-hiccup [:span])
         :code transform-code
         :blockquote transform-quote
         :table (partial md/into-hiccup [:table.table])
         :image transform-img
         :html-inline (comp hiccup/raw md/node->text)
         :html-block (comp hiccup/raw md/node->text)))

(defn parse-raw
  "Parses raw markdown, i.e. without header.  Returns a hiccup structure."
  [s renderers]
  (->> s
       (md/parse {:disable-inline-formulas true})
       (md/->hiccup renderers)))

(defn read-header
  "Given a reader, tries to read the leading metadata edn structure.  Input should be 
   a `java.io.BufferedReader`"
  [^BufferedReader b]
  (let [r (PushbackReader. b)]
    (try
      (.mark b 1000)         ; Support up to 1k buffer for invalid edn
      (let [h (edn/read r)]
        (if (map? h)
          h
          (.reset b)))
      (catch Exception ex
        (if (.startsWith (ex-message ex) "No dispatch macro")
          (.reset b)                   ; No edn at start, so ignore it
          (throw ex))))))

(defn ^BufferedReader buffered [^Reader r]
  (BufferedReader. r))

(defn parse
  "Parses the given markdown content and returns it as a hiccup style structure,
   using the specified renderers.  Any leading edn structure is added to the metadata,
   and the parsed markdown is returned in the `:content`."
  [content renderers]
  (with-open [b (buffered (i/->reader content))]
    (let [meta (read-header b)
          s (slurp b)]
      (->> (parse-raw s renderers)
           (assoc meta :contents)))))
