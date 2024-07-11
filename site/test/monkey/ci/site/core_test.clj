(ns monkey.ci.site.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.site.core :as sut]))

(defn with-tmp-dir* [f]
  (let [dir (fs/create-temp-dir)]
    (try
      (f dir)
      (finally
        (fs/delete-tree dir)))))

(defmacro with-tmp-dir [dir & body]
  `(with-tmp-dir*
     (fn [d#]
       (let [~dir d#]
         ~@body))))

(deftest build
  (with-tmp-dir dest
    (testing "generates html files to destination dir"
      (is (nil? (sut/build {:output dest}))))

    (testing "generates index file"
      (is (fs/exists? (fs/path dest "index.html"))))))
