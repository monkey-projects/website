(ns monkey.ci.template.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [clojure.string :as cs]
            [monkey.ci.template.build :as sut]))

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
      (is (nil? (sut/build {:output dest
                            :site-fn (constantly [:html])}))))

    (testing "generates index file"
      (is (fs/exists? (fs/path dest "index.html"))))

    (testing "applies configured base url"
      (is (nil? (sut/build {:output dest
                            :site-fn (constantly [:html])
                            :config {:base-url "test.monkeyci.com"}})))
      (is (cs/includes? (slurp (fs/file (fs/path dest "index.html")))
                        "https://app.test.monkeyci.com")))))
