(ns monkey.ci.template.components-test
  (:require [clojure.string :as cs]
            [clojure.test :refer [deftest testing is]]
            [monkey.ci.template.components :as sut]))

(def test-config
  {:base-url "test.monkeyci.com"})

(deftest header-dark
  (testing "creates header component"
    (is (= :header
           (-> (sut/header-dark test-config)
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

(deftest script-url
  (testing "build assets js url"
    (is (= "https://assets.test.monkeyci.com/js/test-script.js"
           (sut/script-url test-config "test-script.js")))))

(deftest not-found
  (testing "generates component"
    (is (vector? (sut/not-found {})))))

(deftest not-found-page
  (testing "generates html page"
    (is (= :html (-> (sut/not-found-page {})
                     first)))))
