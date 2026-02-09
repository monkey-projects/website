(ns monkey.ci.docs.md
  "Markdown processing"
  (:require [clojure.edn :as edn]
            [monkey.ci.docs.config :as dc]
            [monkey.ci.template.md :as tmd]
            [nextjournal.markdown.transform :as mdt]))

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

(defn parse-raw [content & [opts]]
  (tmd/parse-raw content (hiccup-renderers opts)))

(defn parse
  "Parses the given markdown content and returns it as a hiccup style structure.
   Any leading edn structure is added to the metadata.  Extra options can be
   specified for transformations."
  [content & [opts]]
  (tmd/parse content (hiccup-renderers opts)))
