(ns monkey.ci.docs.main-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.docs.main :as sut]))

(deftest breadcrumb
  (testing "renders base breadcrumb when empty arg"
    (is (= [:nav
            [:ol.breadcrumb.mb-0
             [:li.breadcrumb-item [:a {:href "/"} "Home"]]]]
           (sut/breadcrumb [])))))

(deftest main
  (testing "generates html page"
    (is (= :html (first (sut/main {}))))))
