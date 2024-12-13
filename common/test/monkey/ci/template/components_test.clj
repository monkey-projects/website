(ns monkey.ci.template.components-test
  (:require [clojure.string :as cs]
            [clojure.test :refer [deftest testing is]]
            [monkey.ci.template.components :as sut]))

(def test-config
  {:base-url "test.monkeyci.com"})

(deftest header
  (testing "creates header component"
    (is (= :header
           (-> (sut/header test-config)
               (first)
               (name)
               (cs/split #"#")
               (first)
               (keyword))))))

(deftest footer
  (testing "creates footer component"
    (is (= :footer
           (-> (sut/footer test-config)
               (first)
               (name)
               (cs/split #"\.")
               (first)
               (keyword))))))

(deftest site-url
  (testing "generates full site url with path"
    (is (= "https://www.test.monkeyci.com/test/path"
           (sut/site-url test-config
                         "/test/path")))))
