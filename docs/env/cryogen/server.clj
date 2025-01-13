(ns server
  (:require [cryogen.server :as cs]
            [cryogen-core.compiler :as ccc]))

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
