(ns monkey.ci.template.svg-test
  (:require [monkey.ci.template.svg :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest include
  (testing "loads svg from configured path"
    (is (= :svg
           (-> (sut/include {:svg-path "dev-resources/svg"}
                            "test")
               (first))))))
