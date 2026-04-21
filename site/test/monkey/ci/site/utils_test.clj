(ns monkey.ci.site.utils-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.site.utils :as sut]))

(deftest make-same-line-count
  (testing "expands with empty lines"
    (let [r (sut/make-same-line-count
             [["long" "text"]
              ["short text"]])]
      (is (= 2 (count r)))
      (is (every? (comp (partial = 2) count) r)))))
