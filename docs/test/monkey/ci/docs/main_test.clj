(ns monkey.ci.docs.main-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.docs.main :as sut]))

(deftest main
  (testing "generates html page"
    (is (= :html (first (sut/main {}))))))
