(ns monkey.ci.docs.edn-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.docs
             [edn :as sut]
             [test-helpers :as h]]))

(deftest parse
  (h/with-tmp-dir tmp
    (testing "parses edn file as-is"
      (let [p (fs/path tmp "test.edn")
            c {:title "test file"
               :contents [:div "test contents"]}]
        
        (is (nil? (spit (fs/file p) (pr-str c))))
        (is (= c (sut/parse p {})))))

    (testing "parses markdown parts"
      (let [p (fs/path tmp "md.edn")]
        (is (nil? (spit (fs/file p) (pr-str {:contents [:md "This is markdown"]}))))
        (is (= :p
               (-> (sut/parse p {})
                   :contents
                   second
                   first)))))

    (testing "generates components"
      (let [p (fs/path tmp "md.edn")]
        (is (nil? (spit (fs/file p) (pr-str {:contents [:alert/warn "test alert"]}))))
        (is (= :div.alert.d-flex.gap-4
               (-> (sut/parse p {})
                   :contents
                   first)))))))
