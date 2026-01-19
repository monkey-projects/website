(ns monkey.ci.docs.edn
  "Functions for processing edn content"
  (:require [clojure
             [edn :as edn]
             [walk :as w]]
            [monkey.ci.docs
             [md :as md]
             [input :as i]]))

(defn- markdown? [x]
  (and (vector? x)
       (= :md (first x))))

(defn- process-md [c conf]
  (w/postwalk (fn [x]
                (if (markdown? x)
                  (md/parse-raw (second x))
                  x))
              c))

(defn parse [content opts]
  (with-open [r (java.io.PushbackReader. (i/->reader content))]
    (-> (edn/read r)
        (update :contents process-md opts))))
