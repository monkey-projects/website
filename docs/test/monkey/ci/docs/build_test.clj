(ns monkey.ci.docs.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.docs.build :as sut]
            [monkey.ci.template.utils :refer [with-tmp-dir]]))

(deftest output-path
  (testing "calcules directory relative to output according to input"
    (is (= "test-output/a/b/index.html"
           (str (sut/output-path (fs/path "test-input/a/b.md")
                                 {:output "test-output"
                                  :input "test-input"}))))))

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
             (-> loc last :path))))

    (testing "includes category if provided"
      (let [loc (sut/location {:title "Some article"
                               :category :test-category}
                              (fs/path "test-article.md")
                              {:categories {:test-category
                                            {:label "Test category"}}})]
        (is (= 3 (count loc)))
        (is (= "Test category"
               (-> loc (second) :label)))
        (is (= "/categories/test-category"
               (-> loc (second) :path)))))

    (testing "fails when category not found"
      (is (thrown? Exception
                   (sut/location {:title "some article"
                                  :category :nonexisting}
                                 (fs/path "test-article.md")
                                 {}))))))

(deftest configure-categories
  (let [cats {:test-category {:label "test category"}}]
    (testing "adds files to categories"
      (let [test-file {:md {:category :test-category}}]
        (is (= [test-file] (-> {:files [test-file]
                                :categories cats}
                               (sut/configure-categories)
                               :test-category
                               :files)))))

    (testing "ignores files that don't have a category"
      (is (empty? (-> {:files [{:title "test file"}]
                       :categories cats}
                      (sut/configure-categories)
                      :test-category
                      :files))))

    (testing "adds location"
      (is (= {:path "/categories/test-category"
              :label "test category"}
             (-> {:categories cats}
                 (sut/configure-categories)
                 :test-category
                 :location
                 last))))))

(deftest build-all
  (with-tmp-dir dir
    (let [res (sut/build-all {:output dir})]
      (is (not-empty (:articles res)))
      (is (not-empty (:categories res)))

      (testing "creates index.html file in output dir"
        (is (fs/exists? (fs/path dir "index.html"))))

      (testing "creates files in `/categories` directory"
        (let [cat-dir (fs/path dir "categories")]
          (is (fs/directory? cat-dir))))

      (testing "creates files in `/articles` directory"
        (let [art-dir (fs/path dir "articles")]
          (is (fs/directory? art-dir))))

      (testing "creates file for each markdown file in mirror directory structure"
        (is (fs/exists? (fs/path dir "articles/intro/basic-example/index.html")))))))

