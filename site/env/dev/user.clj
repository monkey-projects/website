(ns user
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [monkey.ci.template.build :as tb]
            [monkey.ci.site
             [about :as sa]
             [main :as sm]]
            [monkey.ci.template.components :as tc]))

(defn- load-config []
  (with-open [in (java.io.PushbackReader. (io/reader (io/resource "config.edn")))]
    (edn/read in)))

(defn build-site
  "Builds the site to target directory"
  []
  (tb/site {:output "target"
            :pages {"index" sm/main
                    "about" sa/about
                    "error-404" tc/not-found-page}
            :config (merge
                     {:base-url "staging.monkeyci.com"
                      :api-url "http://localhost:3000"}
                     (load-config))}))
