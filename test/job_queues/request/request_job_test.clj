(ns job-queues.request.request-job-test
  (:require [clojure.test :refer :all]
            [job-queues.request.helpper :refer [do-request]]
            [job-queues.mock :as mock]
            [job-queues.database :refer [clean-db]]
            [clojure.string :refer [includes?]]
            [job-queues.fixture :refer [database-test-fixture]]
            [cheshire.core :refer [generate-string]]))

(use-fixtures :once database-test-fixture)

(deftest post-request-jobs-wrong-body-test
  (testing "test post to request_jobs endpoint with wrong json body."
    (let [invalid-body {:foobar "barfoo"}
          result (do-request :post "/request_jobs" invalid-body)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-request-jobs-invalid-param-test
  (testing "test post to request_jobs endpoint with invalid param type (`agent_id` num) in json body."
    (let [invalid-request-job  {:agent_id 123}
          result (do-request :post "/request_jobs" invalid-request-job)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-request-jobs-without-any-agent-test
  (testing "test post a request_jobs endpoint without any agent in database."
    (clean-db)
    (let [request-job {:agent_id mock/agent-id-A}
          result (do-request :post "/request_jobs" request-job)]
      (is (= (:status result) 404))
      (is (includes? (:body result) "Agent not found")))))

(deftest post-request-jobs-without-any-jobs-test
  (testing "test post a request_jobs endpoint without any jobs."
    (clean-db)
    (do-request :post "/agents" mock/agent-A)
    (do-request :post "/agents" mock/agent-B)
    (let [result-1 (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
          result-2 (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
          expect-status 200
          expect-headers {"Content-Type" "application/json"}
          expect-body-1 (generate-string {:job_id nil :agent_id mock/agent-id-A})
          expect-body-2 (generate-string {:job_id nil :agent_id mock/agent-id-B})]
      (is (= result-1 {:status expect-status :headers expect-headers :body expect-body-1}))
      (is (= result-2 {:status expect-status :headers expect-headers :body expect-body-2})))))

(deftest post-request-jobs-test
  (testing "test post a request_jobs endpoint with some jobs and agents in db."
    (clean-db)
    (do-request :post "/agents" mock/agent-A)
    (do-request :post "/agents" mock/agent-B)
    (do-request :post "/jobs" mock/job-A)
    (do-request :post "/jobs" mock/job-B)
    (do-request :post "/jobs" mock/job-C)
    (let [result-1 (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
          result-2 (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
          result-3 (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
          result-4 (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
          result-5 (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
          expect-status 200
          expect-headers {"Content-Type" "application/json"}
          expect-body-1 (generate-string {:job_id mock/job-id-C :agent_id mock/agent-id-A})
          expect-body-2 (generate-string {:job_id mock/job-id-A :agent_id mock/agent-id-B})
          expect-body-3 (generate-string {:job_id mock/job-id-B :agent_id mock/agent-id-A})
          expect-body-4 (generate-string {:job_id nil :agent_id mock/agent-id-A})
          expect-body-5 (generate-string {:job_id nil :agent_id mock/agent-id-B})]
      (is (= result-1 {:status expect-status :headers expect-headers :body expect-body-1}))
      (is (= result-2 {:status expect-status :headers expect-headers :body expect-body-2}))
      (is (= result-3 {:status expect-status :headers expect-headers :body expect-body-3}))
      (is (= result-4 {:status expect-status :headers expect-headers :body expect-body-4}))
      (is (= result-5 {:status expect-status :headers expect-headers :body expect-body-5})))))
