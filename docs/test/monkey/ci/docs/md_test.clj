(ns monkey.ci.docs.md-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [com.rpl.specter :as s]
            [babashka.fs :as fs]
            [hiccup-find.core :as hf]
            [monkey.ci.docs.md :as sut]))

(deftest parse
  (let [md "# Test page\nTest contents"
        expected [:div
                  [:h4 {:id "test-page"} "Test page"]
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
               :title))))

  (testing "rewrites relative links to include configured path prefix"
    (is (= "/test-prefix/articles/a/b/c"
           (->> (sut/parse "{}\n[test link](a/b/c)" {:path-prefix "/test-prefix/"})
                :contents
                ;; Look up the href for the first :a tag
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 0 1) :href])
                first))))

  (testing "rewrites relative links to include configured articles prefix"
    (is (= "/test-prefix/a/b/c"
           (->> (sut/parse "{}\n[test link](a/b/c)" {:articles-prefix "/test-prefix/"})
                :contents
                ;; Look up the href for the first :a tag
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 0 1) :href])
                first))))

  (testing "leaves absolute refs unchanged"
    (is (= "/a/b/c"
           (->> (sut/parse "{}\n[test link](/a/b/c)" {:path-prefix "/test-prefix/"})
                :contents
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 0 1) :href])
                first))))

  (testing "leaves external urls unchanged"
    (is (= "http://a/b/c"
           (->> (sut/parse "{}\n[test link](http://a/b/c)" {:path-prefix "/test-prefix/"})
                :contents
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 0 1) :href])
                first)))))
