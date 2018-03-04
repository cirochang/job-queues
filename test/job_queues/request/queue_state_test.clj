(ns job-queues.request.queue-state-test
  (:require [clojure.test :refer :all]
            [job-queues.request.helpper :refer [do-request]]
            [job-queues.mock :as mock]
            [job-queues.database :refer [clean-db]]
            [clojure.string :refer [includes?]]
            [job-queues.fixture :refer [database-test-fixture]]
            [cheshire.core :refer [generate-string parse-string]]))

(use-fixtures :once database-test-fixture)

(deftest get-queue-state-without-data-test
  (testing "test get a queue_state endpoint without any jobs or agents in db."
    (clean-db)
    (let [result (do-request :get "/queue_state")]
      (is (= result {:status 200 :headers {"Content-Type" "application/json"} :body "{}"})))))

(deftest get-queue-state-without-job-assigned-test
  (testing "test get a queue_state endpoint without any assigned job db."
    (clean-db)
    (do-request :post "/jobs" mock/job-A)
    (do-request :post "/jobs" mock/job-B)
    (do-request :post "/jobs" mock/job-C)
    (let [result (do-request :get "/queue_state")
          result-seq (parse-string (:body result))
          result-jobs-id-waiting (map #(get % "id") (get result-seq "waiting"))
          expect-jobs-id-waiting [mock/job-id-A mock/job-id-B mock/job-id-C]]
      (is (= (:status result) 200))
      (is (= result-jobs-id-waiting expect-jobs-id-waiting)))))

(deftest get-queue-state-test
  (testing "test get a queue_state endpoint when jobs has differents status db."
    (clean-db)
    (do-request :post "/agents" mock/agent-A)
    (do-request :post "/agents" mock/agent-B)
    (do-request :post "/jobs" mock/job-A)
    (do-request :post "/jobs" mock/job-B)
    (do-request :post "/jobs" mock/job-C)
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
    (let [result (do-request :get "/queue_state")
          result-seq (parse-string (:body result))
          result-jobs-id-waiting (map #(get % "id") (get result-seq "waiting"))
          result-jobs-id-being-done (map #(get % "id") (get result-seq "being done"))
          result-jobs-id-completed (map #(get % "id") (get result-seq "completed"))]
      (is (= (:status result) 200))
      (is (= result-jobs-id-waiting []))
      (is (= result-jobs-id-being-done [mock/job-id-B mock/job-id-C]))
      (is (= result-jobs-id-completed [mock/job-id-A])))))
