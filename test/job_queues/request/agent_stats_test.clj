(ns job-queues.request.agent-stats-test
  (:require [clojure.test :refer :all]
            [job-queues.request.helpper :refer [do-request]]
            [job-queues.mock :as mock]
            [job-queues.database :refer [clean-db]]
            [clojure.string :refer [includes?]]
            [job-queues.fixture :refer [database-test-fixture]]
            [cheshire.core :refer [parse-string]]))

(use-fixtures :once database-test-fixture)

(deftest get-agent-stats-without-data-test
  (testing "test get agent_stats endpoint with an agent that is not in db."
    (clean-db)
    (let [endpoint (str "/agent_stats/" mock/agent-id-A)
          result (do-request :get endpoint)]
      (is (= result {:status 200 :headers {"Content-Type" "application/json"} :body "{}"})))))

(deftest get-agent-stats-test
  (testing "test get agent_stats endpoint with some jobs already assigned db."
    (clean-db)
    (do-request :post "/agents" mock/agent-A)
    (do-request :post "/agents" mock/agent-B)
    (do-request :post "/jobs" mock/job-A)
    (do-request :post "/jobs" mock/job-B)
    (do-request :post "/jobs" mock/job-C)
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-A})
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
    (do-request :post "/request_jobs" {:agent_id mock/agent-id-B})
    (let [endpoint-1 (str "/agent_stats/" mock/agent-id-A)
          endpoint-2 (str "/agent_stats/" mock/agent-id-B)
          result-1 (do-request :get endpoint-1)
          result-1-seq (parse-string (:body result-1))
          result-2 (do-request :get endpoint-2)
          result-2-seq (parse-string (:body result-2))]
      (is (= (:status result-1) 200))
      (is (= result-1-seq {"bills-questions" 1}))
      (is (= (:status result-2) 200))
      (is (= result-2-seq {"rewards-question" 1 "bills-questions" 1})))))
