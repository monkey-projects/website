(ns build-test
  (:require [clojure.test :refer [deftest testing is]]
            [build :as sut]
            [monkey.ci.test :as mt]
            [monkey.ci.build
             [core :as bc]
             [v2 :as m]]))

(deftest test-common
  (testing "`nil` if common is unchanged"
    (is (nil? (sut/test-common {}))))

  (let [ctx (-> mt/test-ctx
                (mt/with-changes (mt/modified ["common/src/core.clj"])))]
    
    (testing "clj container if common files have changed"
      (is (bc/container-job? (sut/test-common ctx))))

    (testing "has `test-common` id"
      (is (= "test-common" (bc/job-id (sut/test-common ctx)))))))

(deftest deploy-common
  (mt/with-build-params {}
    (testing "`nil` if common is unchanged"
      (is (nil? (sut/deploy-common mt/test-ctx))))

    (testing "`nil` if common is changed, but not on main branch or tag"
      (is (nil? (-> mt/test-ctx
                    (mt/with-git-ref "refs/heads/other")
                    (mt/with-changes (mt/modified ["common/some-file"]))
                    (sut/deploy-common)))))

    (testing "when on main branch and files changed"
      (let [ctx (-> mt/test-ctx
                    (mt/with-git-ref "refs/heads/main")
                    (mt/with-changes (mt/modified ["common/some-file"])))]
        (testing "creates container job "
          (is (bc/container-job? (sut/deploy-common ctx))))

        (testing "depends on common test job"
          (is (= ["test-common"] (:dependencies (sut/deploy-common ctx)))))))))

(deftest test-site
  (testing "creates container job"
    (is (bc/container-job? (sut/test-site mt/test-ctx)))))

(deftest test-docs
  (testing "creates container job"
    (is (bc/container-job? (sut/test-docs mt/test-ctx)))))

(deftest build-site
  (let [job (sut/build-site mt/test-ctx)]
    (testing "creates container job"
      (is (bc/container-job? job)))
    
    (testing "renders site and 404 page"
      (is (= 1 (-> job
                   (m/script)
                   (count)))))))

(deftest notify
  (testing "`nil` if not a release tag"
    (is (nil? (sut/notify mt/test-ctx))))

  (testing "creates pushover job"
    (is (bc/action-job?
         (-> mt/test-ctx
             (mt/with-git-ref "refs/tags/0.1.0")
             (sut/notify))))))
