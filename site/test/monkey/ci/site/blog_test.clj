(ns monkey.ci.site.blog-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [monkey.ci.site.blog :as sut]))

(defn with-tmp-dir* [f]
  (let [dir (fs/create-temp-dir)]
    (try
      (f dir)
      (finally
        (fs/delete-tree dir)))))

(defmacro with-tmp-dir [dir & body]
  `(with-tmp-dir*
     (fn [~dir]
       ~@body)))

(deftest blog-pages
  (with-tmp-dir dir
    (let [conf {:src (fs/path dir "src")
                :dest (fs/path dir "dest")}]
      (is (nil? (spit (fs/file (fs/create-dirs (:src conf)) "test.md") "This is a test")))
      (let [r (sut/blog-pages {:blog conf})]
        (is (not-empty r))
        
        (testing "generates directory per page"
          (for [f r]
            (is (fs/exists? (:dest f)))))

        (testing "generates archive page"
          (is (fs/exists? (fs/path (:dest conf) "archive"))))

        (testing "puts last blog as index"
          (is (fs/exists? (fs/path (:dest conf) "index.html")))))

      (testing "ignores unsupported extensions"
        (is (some? (fs/move (fs/path (:src conf) "test.md")
                            (fs/path (:src conf) "test.txt"))))
        (is (empty? (sut/blog-pages {:blog conf})))))))

(deftest generate-blog
  (with-tmp-dir dir
    (let [p (fs/path dir "test.md")]
      (is (nil? (spit (fs/file p) (str (pr-str {:title "test article"} "\nThis is a test article")))))
      (let [r (sut/generate-blog {} p)]
        (testing "reads header from contents"
          (is (= "test article" (:title r))))

        (testing "adds contents"
          (is (some? (:contents r))))))))

(deftest dest-file
  (testing "includes original file name without extension"
    (is (= "test-entry"
           (sut/dest-file {:src "/tmp/some/dir/test-entry.md"})))))
