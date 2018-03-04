(ns job-queues.request.agent-test
  (:require [clojure.test :refer :all]
            [job-queues.request.helpper :refer [do-request]]
            [job-queues.mock :as mock]
            [clojure.string :refer [includes?]]
            [job-queues.fixture :refer [database-test-fixture]]))

(use-fixtures :once database-test-fixture)

(deftest post-agents-wrong-body-test
  (testing "test post to agents endpoint with wrong json body."
    (let [invalid-body {:foo "bar"}
          result (do-request :post "/agents" invalid-body)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-agents-invalid-param-test
  (testing "test post to agents endpoint with invalid param type (`name` bool) in json body."
    (let [invalid-agent (assoc mock/agent-A :name true)
          result (do-request :post "/agents" invalid-agent)]
      (is (= (:status result) 400))
      (is (includes? (:body result) "Value does not match schema")))))

(deftest post-agents-test
  (testing "test post an `agent` to agents endpoint."
    (let [result (do-request :post "/agents" mock/agent-A)]
        (is (= (:status result) 200)))))

(deftest post-agents-duplicate-test
  (testing "test post two times the same `agent` to agents endpoint."
    (let [first-result (do-request :post "/agents" mock/agent-B)
          second-result (do-request :post "/agents" mock/agent-B)]
        (is (= (:status first-result) 200))
        (is (= (:status second-result) 409))
        (is (includes? (:body second-result) "This agent already exists in database")))))
