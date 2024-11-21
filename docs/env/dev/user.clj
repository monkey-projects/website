(ns user
  (:require [cryogen.server :as cs]
            [cryogen-core.compiler :as ccc]
            [monkey.ci.template.build :as tb]))

(defn build-docs
  "Builds the docs to target directory"
  []
  (tb/build {:output "target"
             :site-fn 'monkey.ci.docs.main/main
             :config {:base-url "staging.monkeyci.com"
                      :api-url "http://localhost:3000"}}))

(defonce server (atom nil))

(defn stop [s]
  (when s
    (.stop s)))

(defn stop! []
  (swap! server stop))

(defn serve!
  "Starts cryogen server for fast content editing"
  []
  (swap! server (fn [s]
                  (stop s)
                  (cs/serve {:join? false}))))

(defn rebuild
  "Performs a full build of the cryogen content."
  []
  (ccc/compile-assets-timed))
