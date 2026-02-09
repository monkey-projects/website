(ns monkey.ci.template.md
  "Markdown processing"
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [hiccup2.core :as hiccup]
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

(def transform-quote (partial mdt/into-markup [:blockquote.blockquote.blockquote-sm.mb-2]))

(defn- transform-img [ctx node]
  (let [orig (:image mdt/default-hiccup-renderers)]
    (->> (orig ctx node)
         (vector :div.shadow.text-center))))

(def hiccup-renderers
  (assoc mdt/default-hiccup-renderers
         :heading transform-heading
         :plain (partial mdt/into-markup [:span])
         :code transform-code
         :blockquote transform-quote
         :table (partial mdt/into-markup [:table.table])
         :image transform-img
         :html-inline (comp hiccup/raw md/node->text)
         :html-block (comp hiccup/raw md/node->text)))

(defn parse-raw
  "Parses raw markdown, i.e. without header.  Returns a hiccup structure."
  [s renderers]
  (->> s
       (md/parse)
       (mdt/->hiccup renderers)))
