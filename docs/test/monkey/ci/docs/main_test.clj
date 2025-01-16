(ns monkey.ci.docs.main-test
  (:require [clojure.test :refer [deftest testing is]]
            [com.rpl.specter :as s]
            [hiccup-find.core :as hf]
            [monkey.ci.docs.main :as sut]))

(deftest breadcrumb
  (testing "renders base breadcrumb when empty arg"
    (is (= [:nav
            [:ol.breadcrumb.mb-0
             [:li.breadcrumb-item [:a {:href "/"} "Home"]]]]
           (sut/breadcrumb [] {}))))

  (testing "uses document path in href"
    (is (= "test/path"
           (->> (sut/breadcrumb [{:path "test/path"
                                  :label "Test Doc"}]
                                {})
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 1 1) :href])
                first))))

  (testing "applies configured path prefix"
    (is (= "/base/test/path"
           (->> (sut/breadcrumb [{:path "test/path"
                                  :label "Test Doc"}]
                                {:path-prefix "/base/"})
                (hf/hiccup-find [:a])
                (s/select [(s/nthpath 1 1) :href])
                first)))))

(deftest md->page
  (testing "generates html page"
    (is (= :html (first (sut/md->page [:h1 "test page"] {}))))))
