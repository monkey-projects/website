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
      (is (not-empty (hf/hiccup-find [:footer] page)))))

  (testing "adds table of contents if provided"
    (is (not-empty (->> {:toc [{:path "path" :title "Title"}]}
                        (sut/md->page [:h1 "page with toc"])
                        (hf/hiccup-find [:#toc]))))))

(deftest short-title
  (testing "returns short before title"
    (is (= "short title" (sut/short-title {:short "short title"
                                           :title "long title"})))
    (is (= "long title" (sut/short-title {:title "long title"})))))

(deftest mark-active
  (testing "marks page active according to location"
    (let [toc [{:path "/"
                :title "Home"}
               {:path "/test"
                :title "Test"}]
          page {:contents [:h1 "Test page"]
                :location [{:path "/test" :label "Test"}]}
          act (sut/mark-active toc page)]
      (is (true? (-> act
                     (last)
                     :active?)))
      (is (not (true? (-> act
                          (first)
                          :active?)))))))
