(ns build
  (:require [monkey.ci.plugin.clj :as clj]))

;; Use available plugins or create your own
;; This creates two jobs: one for unit tests, another to publish
(clj/deps-library)
