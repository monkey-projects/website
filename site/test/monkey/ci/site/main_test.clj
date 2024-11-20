(ns monkey.ci.site.main-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.site.main :as sut]))

(deftest main
  (testing "creates html page"
    (is (= :html (first (sut/main {}))))))
