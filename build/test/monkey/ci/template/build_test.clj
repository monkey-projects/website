(ns monkey.ci.template.build-test
  (:require [babashka.fs :as fs]
            [clojure
             [string :as cs]
             [test :refer [deftest is testing]]]
            [monkey.ci.template
             [build :as sut]
             [utils :refer [with-tmp-dir]]]))

(deftest site
  (with-tmp-dir dest
    (testing "generates html files to destination dir"
      (is (some? (sut/site {:output dest
                            :site-fn (constantly [:html])}))))

    (testing "generates index file"
      (is (fs/exists? (fs/path dest "index.html"))))

    (testing "adds doctype declaration"
      (is (-> (fs/path dest "index.html")
              (fs/file)
              (slurp)
              (cs/starts-with? "<!DOCTYPE html>"))))

    (testing "passes config to site fn"
      (is (some? (sut/site {:output dest
                            :site-fn (fn [conf]
                                       [:html
                                        [:head
                                         [:title (:base-url conf)]]])
                            :config {:base-url "test.monkeyci.com"}})))
      (is (cs/includes? (slurp (fs/file (fs/path dest "index.html")))
                        "test.monkeyci.com"))))

  (with-tmp-dir dest
    (testing "with pages map"
      (testing "generates page per entry"
        (is some? (sut/site {:output dest
                             :pages {"index" (constantly [:html [:body "Index"]])
                                     "about" (constantly [:html [:body "About"]])}}))
        (is (fs/exists? (fs/path dest "index.html")))
        (is (fs/exists? (fs/path dest "about.html")))))))

