(ns monkey.ci.site.md-test
  (:require [monkey.ci.site.md :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest md-page
  (testing "parses markdown into hiccup structure"
    (is (vector? (sut/md-page "# Test page")))))

(deftest md-resource
  (testing "parses resource file into hiccup"
    (is (vector? (sut/md-resource "md/terms-of-use.md")))))
