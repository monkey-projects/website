(ns monkey.ci.docs.md-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [babashka.fs :as fs]
            [monkey.ci.docs.md :as sut]))

(deftest parse
  (let [md "# Test page\nTest contents"
        expected [:div
                  [:h1 {:id "test-page"} "Test page"]
                  [:p "Test contents"]]]
    
    (testing "parses markdown content from string to hiccup structure"
      (is (= expected
             (:contents (sut/parse "# Test page\nTest contents")))))

    (testing "parses markdown content from file to hiccup structure"
      (let [f (fs/create-temp-file)]
        (is (nil? (spit (fs/file f) md)))
        (try
          (is (= expected
                 (:contents (sut/parse f))))
          (finally
            (fs/delete f)))))

    (testing "parses hiccup content from reader"
      (with-open [r (io/reader (.getBytes md))]
        (is (= expected
               (:contents (sut/parse r)))))))

  (testing "includes leading edn into metadata"
    (is (= "test title"
           (-> {:title "test title"}
               pr-str
               (str "\nTest contents")
               (sut/parse)
               :title)))))
