(ns monkey.ci.docs.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.docs.build :as sut]
            [monkey.ci.template.utils :refer [with-tmp-dir]]))

(deftest output-path
  (testing "calcules directory relative to output according to input"
    (is (= "test-output/a/b/index.html"
           (str (sut/output-path {}
                                 (fs/path "test-input/a/b.md")
                                 {:output "test-output"
                                  :input "test-input"})))))

  (testing "puts home file in root"
    (is (= "test-output/index.html"
           (str (sut/output-path {:home? true}
                                 (fs/path "test-input/home.md")
                                 {:output "test-output"}))))))

(deftest location
  (testing "root for home doc"
    (is (= [{:path "/"
             :label "Home"}]
           (sut/location {:home? true}
                         (fs/path "test-input/home.md")
                         {}))))

  (let [loc (sut/location {:title "Test doc"}
                          (fs/path "test-input/a/b.md")
                          {:input "test-input"})]
    
    (testing "takes title from doc"
      (is (= "Test doc"
             (-> loc last :label))))

    (testing "applies default articles prefix from config"
      (is (= "/articles/a/b/"
             (-> loc last :path))))))

(deftest build-all
  (with-tmp-dir dir
    (is (every? some? (sut/build-all {:output dir})))

    (testing "creates index.html file in output dir"
      (is (fs/exists? (fs/path dir "index.html"))))

    (testing "creates files in `/categories` directory"
      (let [cat-dir (fs/path dir "categories")]
        (is (fs/directory? cat-dir))))

    (testing "creates files in `/articles` directory"
      (let [art-dir (fs/path dir "articles")]
        (is (fs/directory? art-dir))))

    (testing "creates file for each markdown file in mirror directory structure"
      (is (fs/exists? (fs/path dir "articles/intro/basic-example/index.html"))))))

