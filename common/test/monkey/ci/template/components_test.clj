(ns monkey.ci.template.components-test
  (:require [clojure.string :as cs]
            [clojure.test :refer [deftest testing is]]
            [monkey.ci.template.components :as sut]))

(deftest header
  (testing "creates header component"
    (is (= :header
           (-> (sut/header {})
               (first)
               (name)
               (cs/split #"#")
               (first)
               (keyword))))))

