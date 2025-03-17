(ns user
  (:require [aleph.http :as http]
            [babashka.fs :as fs]
            [clojure.tools.logging :as log]
            [hawk.core :as hawk]
            [monkey.ci.docs.build :as db]
            [ring.util.response :as rur]))

(def doc-root "target/site")

(defn build-docs
  "Builds the docs to target directory"
  []
  (-> {:output doc-root
       :config {:base-url "staging.monkeyci.com"
                :api-url "http://localhost:3000"
                :path-prefix "/"}}
      (db/build-all)
      (dissoc :files)))

(defn handler [req]
  (log/info "Handling:" (:uri req))
  (if (= :get (:request-method req))
    (or (rur/file-response (:uri req) {:root doc-root})
        (rur/status 404))
    (rur/status 405)))

(defn serve
  "Starts a local http server that hosts the documentation site"
  [& [opts]]
  (http/start-server handler (merge {:port 8090} opts)))

(defonce server (atom nil))

(defn stop! []
  (swap! server (fn [s]
                  (when s
                    (.close s)))))

(defn serve! [& [opts]]
  (swap! server (fn [s]
                  (when s
                    (.close s))
                  (serve opts))))

(defonce watcher (atom nil))

(defn- watch-handler [ctx {:keys [file]}]
  (when-not (re-matches #"^[\.#].*$" (fs/file-name file))
    (log/debug "File change:" file)
    (build-docs))
  ctx)

(defn watch!
  "Watches the content directory for changes, and auto rebuilds"
  []
  (swap! watcher
         (fn [w]
           (when w
             (hawk/stop! w))
           (hawk/watch! [{:paths ["content"]
                          :handler watch-handler}]))))

(defn stop-watch! []
  (swap! watcher
         (fn [w]
           (when w
             (hawk/stop! w)
             nil))))
