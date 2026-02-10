(ns monkey.ci.template.md-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.ci.template
             [input :as i]
             [md :as sut]]))

(deftest read-header
  (testing "returns edn header of reader"
    (let [v {::key "value"}]
      (is (= v (-> (str (pr-str v) "\nAnd this is more content")
                   (i/->reader)
                   (sut/buffered)
                   (sut/read-header))))))

  (testing "returns `nil` if no header"
    (is (nil? (-> "This is not edn"
                  (i/->reader)
                  (sut/buffered)
                  (sut/read-header))))))
