(ns monkey.ci.docs.test-helpers
  (:require [babashka.fs :as fs]))

(defmacro with-tmp-dir [dir & body]
  `(let [~dir (fs/create-temp-dir)]
     (try
       ~@body
       (finally
         (fs/delete-tree ~dir)))))
