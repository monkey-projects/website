(ns monkey.ci.site.template-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.site.template :as sut]))

(deftest wrap-template
  (testing "wraps in html tag"
    (is (= :html (-> (sut/wrap-template {} [:div "test"])
                     (first))))))
