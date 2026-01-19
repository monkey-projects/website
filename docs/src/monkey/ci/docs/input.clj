(ns monkey.ci.docs.input
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]))

(defprotocol InputSource
  (->reader [x]))

(extend-type java.lang.String
  InputSource
  (->reader [s]
    (java.io.StringReader. s)))

(extend-type java.nio.file.Path
  InputSource
  (->reader [p]
    (io/reader (fs/file p))))

(extend-type java.io.Reader
  InputSource
  (->reader [r]
    r))
