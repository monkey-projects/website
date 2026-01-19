(ns monkey.ci.docs.main-test
  (:require [clojure.test :refer [deftest testing is]]
            [com.rpl.specter :as s]
            [hiccup-find.core :as hf]
            [monkey.ci.docs.main :as sut]))

(deftest breadcrumb
  (testing "empty when empty arg"
    (is (= [:nav
            [:ol.breadcrumb.mb-0]]
           (sut/breadcrumb [] {}))))

  (testing "uses document path in href"
    (is (= "test/path"
           (->> (sut/breadcrumb [{:path "test/path"
                                  :label "Test Doc"}]
                                {})
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 0 1) :href])
                first)))))

(deftest md->page
  (let [page (sut/md->page [:h1 "test page"] {})]
    (testing "generates html page"
      (is (= :html (first page))))

    (testing "page contains header"
      (is (not-empty (hf/hiccup-find [:header] page))))

    (testing "page contains footer"
      (is (not-empty (hf/hiccup-find [:footer] page))))))

(deftest category-page
  (testing "adds categories"
    (is (not-empty (->> {:categories {:test-cat
                                      {:label "test category"}}}
                        (sut/category-page :test-cat)
                        (hf/hiccup-find [:#categories])))))

  (testing "sorts pages by index"
    (is (= ["First" "Second"]
           (->> {:categories
                 {:main
                  {:label "Main category"
                   :files
                   [{:title "Second"
                     :index 1}
                    {:title "First"
                     :index 0}]}}}
                (sut/category-page :main)
                (hf/hiccup-find [:.article-title])
                (map second))))))

(deftest short-title
  (testing "returns short before title"
    (is (= "short title" (sut/short-title {:short "short title"
                                           :title "long title"})))
    (is (= "long title" (sut/short-title {:title "long title"})))))

(deftest index-page
  (testing "renders html page"
    (is (= :html (first (sut/index-page {} {}))))))
