(ns build
  (:require [monkey.ci.api :as m]))

(def unit-test
  (-> (m/container-job "unit-test")
      (m/image "docker.io/maven:4.5")
      (m/script ["mvn verify"])))

;; Single job to execute
[unit-test]
