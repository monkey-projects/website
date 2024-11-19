(ns monkey.ci.docs.core
  (:require [monkey.ci.common.core :as cc]
            [monkey.ci.docs.main :as main]))

(defn build [{:keys [output config]}]
  (cc/build output #(main/main (cc/load-config config))))
