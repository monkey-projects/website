(ns monkey.ci.site.about-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.site.about :as sut]))

(deftest about
  (testing "creates html page"
    (is (= :html (first (sut/about {:svg-path "dev-resources/svg"}))))))
