(ns job-queues.request.job-test
  (:require [clojure.test :refer :all]
            [job-queues.mock :as mock]
            [job-queues.request.helpper :refer [do-request]]
            [clojure.string :refer [includes?]]
            [job-queues.fixture :refer [database-test-fixture]]))

(use-fixtures :once database-test-fixture)

(deftest post-jobs-wrong-body-test
  (testing "test post to jobs endpoint with wrong json body."
    (let [invalid-body {:bar "foo"}
          result (do-request :post "/jobs" invalid-body)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-jobs-invalid-param-test
  (testing "test post to jobs endpoint with invalid param type (`urgent` num) in json body."
    (let [invalid-job (assoc mock/agent-A :urgent 123)
          result (do-request :post "/jobs" invalid-job)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-jobs-test
  (testing "test post an `job` to jobs endpoint."
    (let [result (do-request :post "/jobs" mock/job-A)]
      (is (= (:status result) 200)))))

(deftest post-jobs-duplicate-test
  (testing "test post two times the same `job` to jobs endpoint."
    (let [first-result (do-request :post "/jobs" mock/job-B)
          second-result (do-request :post "/jobs" mock/job-B)]
        (is (= (:status first-result) 200))
        (is (= (:status second-result) 409))
        (is (includes? (:body second-result) "This job already exists in database")))))
