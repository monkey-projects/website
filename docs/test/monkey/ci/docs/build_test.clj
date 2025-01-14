(ns monkey.ci.docs.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.docs.build :as sut]
            [monkey.ci.template.utils :refer [with-tmp-dir]]))

(deftest build-all
  (with-tmp-dir dir
    (is (every? some? (sut/build-all {:output dir})))

    (testing "creates index.html file in output dir"
      (is (fs/exists? (fs/path dir "index.html"))))

    (testing "creates file for each markdown file in mirror directory structure"
      (is (fs/exists? (fs/path dir "intro/basic-example/index.html"))))))
