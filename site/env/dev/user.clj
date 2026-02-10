(ns user
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [monkey.ci.template.build :as tb]
            [monkey.ci.site
             [blog :as blog]
             [core :as sc]
             [about :as sa]
             [main :as sm]]
            [monkey.ci.template.components :as tc]))

(defn- load-config []
  (with-open [in (java.io.PushbackReader. (io/reader (io/resource "config.edn")))]
    (edn/read in)))

(defn test-config []
  (merge
   {:base-url "localhost:8083"
    :assets-url "http://localhost:8083/assets"
    :api-url "http://localhost:3000"}
   (load-config)))

(defn build-site
  "Builds the site to target directory"
  []
  (sc/build {:output "target"
             :pages sc/site-pages
             :config (test-config)}))

(defn gen-test-blog []
  (->> (blog/blog-pages (assoc (test-config)
                               :blog {:src "dev-resources/blog"
                                      :dest "target/blog"}))
       (mapv :dest)))
