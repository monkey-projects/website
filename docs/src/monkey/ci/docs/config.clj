(ns monkey.ci.docs.config)

(defn path-prefix [config]
  (get config :path-prefix "/"))

(defn articles-prefix [config]
  (or (:articles-prefix config)
      (str (path-prefix config) "articles/")))

(defn categories-prefix [config]
  (or (:categories-prefix config)
      (str (path-prefix config) "categories/")))

(defn apply-prefix [path prefix]
  (-> (str prefix path)
      (.replaceAll "//" "/")))

