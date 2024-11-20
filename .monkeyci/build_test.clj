(ns build-test
  (:require [clojure.test :refer [deftest testing is]]
            [build :as sut]
            [monkey.ci.build
             [api :as ba]
             [core :as bc]]))

(defn with-build-params* [params f]
  (with-redefs [ba/build-params (constantly params)]
    (f)))

(defmacro with-build-params
  "Simulates given build parameters for the body to execute"
  [params & body]
  `(with-build-params* ~params (fn [] ~@body)))

(def test-ctx {:build
               {:git
                {:main-branch "main"}}})

(defn with-git-ref [ctx ref]
  (assoc-in ctx [:build :git :ref] ref))

(defn update-changes [ctx f & args]
  (apply update-in ctx [:build :changes] f args))

(defn with-changes [ctx changes]
  (update-changes ctx merge changes))

(defn set-changes [ctx changes]
  (update-changes ctx (constantly changes)))

(defn added [files]
  {:added files})

(defn modified [files]
  {:modified files})

(defn removed [files]
  {:removed files})

(deftest test-common
  (testing "`nil` if common is unchanged"
    (is (nil? (sut/test-common {}))))

  (let [ctx (-> test-ctx
                (with-changes (modified ["common/src/core.clj"])))]
    
    (testing "shell command if common files have changed"
      (is (bc/action-job? (sut/test-common ctx))))

    (testing "has `test-common` id"
      (is (= "test-common" (bc/job-id (sut/test-common ctx)))))))

(deftest deploy-common
  (with-build-params {}
    (testing "`nil` if common is unchanged"
      (is (nil? (sut/deploy-common test-ctx))))

    (testing "`nil` if common is changed, but not on main branch or tag"
      (is (nil? (-> test-ctx
                    (with-git-ref "refs/heads/other")
                    (with-changes (modified ["common/some-file"]))
                    (sut/deploy-common)))))

    (testing "when on main branch and files changed"
      (let [ctx (-> test-ctx
                    (with-git-ref "refs/heads/main")
                    (with-changes (modified ["common/some-file"])))]
        (testing "creates container job "
          (is (bc/container-job? (sut/deploy-common ctx))))

        (testing "depends on common test job"
          (is (= ["test-common"] (:dependencies (sut/deploy-common ctx)))))))))

(deftest test-site
  (testing "creates action job"
    (is (bc/action-job? (sut/test-site {})))))

(deftest test-docs
  (testing "creates action job"
    (is (bc/action-job? (sut/test-docs {})))))

(deftest notify
  (testing "`nil` if not a release tag"
    (is (nil? (sut/notify {}))))

  (testing "creates pushover job"
    (is (bc/action-job?
         (sut/notify {:build
                      {:git
                       {:ref "refs/tags/0.1.0"}}})))))
