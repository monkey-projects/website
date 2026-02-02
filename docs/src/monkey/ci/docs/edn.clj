(ns monkey.ci.docs.edn
  "Functions for processing edn content"
  (:require [clojure
             [edn :as edn]
             [walk :as w]]
            [monkey.ci.docs
             [md :as md]
             [input :as i]]))

(defn- process-md [[_ c] conf]
  (md/parse-raw c))

(defn- alert [type v _]
  [:div.alert.d-flex.gap-4 {:class (str "alert-" (name type))}
   [:i.bi.bi-exclamation-diamond.fs-1]
   (into [:span] (rest v))])

(def alert-warn (partial alert :warning))

(def components
  {:md process-md
   :alert/warn alert-warn})

(defn- component? [x]
  (and (vector? x)
       (contains? components (first x))))

(defn- process-components [c opts]
  (w/prewalk (fn [x]
               (if (component? x)
                 (let [h (get components (first x))]
                   (h x opts))
                 x))
             c))

(defn- first-para [s]
  (when s
    (-> (.split s "\n")
        (first))))

(defn- unwrap-para [v]
  (cond-> v
    (vector? v) (->> (drop-while (some-fn keyword? map?)) first (unwrap-para))
    true (first-para)))

(defn- extract-summary [{:keys [contents] :as d}]
  (assoc d :summary (some-> contents unwrap-para)))

(defn parse [content opts]
  (with-open [r (java.io.PushbackReader. (i/->reader content))]
    (-> (edn/read r)
        (update :contents process-components opts)
        (extract-summary))))
