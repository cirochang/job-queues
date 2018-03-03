(ns job-queues.control-test
  (:require [clojure.test :refer :all]
            [job-queues.control :refer :all]))

(def mock-job-id-A "c0033410-981c-428a-954a-35dec05ef1d2")
(def mock-job-id-B "f26e890b-df8e-422e-a39c-7762aa0bac36")
(def mock-job-A {"id" mock-job-id-A, "type" "bills-questions", "urgent" true})
(def mock-job-B {"id" mock-job-id-B, "type" "rewards-question", "urgent" false})
(def mock-job-C {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false})
(def mock-group-jobs-A [mock-job-A mock-job-B mock-job-C])

(def mock-agent-id-A "8ab86c18-3fae-4804-bfd9-c3d6e8f66260")
(def mock-agent-id-B "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")
(def mock-agent-A {"id" mock-agent-id-A, "name" "BoJack Horseman", "primary_skillset" ["bills-questions"], "secondary_skillset" []})
(def mock-agent-B {"id" mock-agent-id-B, "name" "Mr. Peanut Butter", "primary_skillset" ["rewards-question"], "secondary_skillset" ["bills-questions"]})
(def mock-group-agents-A [mock-agent-A mock-agent-B])

(def mock-job-request-A {"agent_id" mock-agent-id-A})
(def mock-job-request-B {"agent_id" mock-agent-id-B})
(def mock-group-jobs-request-A [mock-job-request-A mock-job-request-B])

(comment
(deftest get-jobs-by-type-test
  (testing "test get correct jobs by type"
    (is (= (get-jobs-by-type mock-group-jobs-A ["bills-questions"]) (list mock-job-A, mock-job-C) ))
    (is (= (get-jobs-by-type mock-group-jobs-A ["rewards-question"]) (list mock-job-B)))))

(deftest get-possible-jobs-by-agent-test
  (testing "test get the possible jobs by agent"
    (is (= (get-possible-jobs-by-agent mock-group-jobs-A mock-agent-A) (list mock-job-A mock-job-C)))
    (is (= (get-possible-jobs-by-agent mock-group-jobs-A mock-agent-B) (list mock-job-B mock-job-A mock-job-C)))))

(deftest get-agent-by-id-test
  (testing "test get correct agent by id"
    (is (= (get-agent-by-id mock-group-agents-A mock-agent-id-A) mock-agent-A))
    (is (= (get-agent-by-id mock-group-agents-A mock-agent-id-B) mock-agent-B))))

(deftest order-jobs-by-urgent-test
  (testing "test order jobs by urgent"
    (is (= (order-jobs-by-urgent [mock-job-B mock-job-A mock-job-C]) (list mock-job-A mock-job-B mock-job-C)))
    (is (= (order-jobs-by-urgent [mock-job-C mock-job-A mock-job-B]) (list mock-job-A mock-job-C mock-job-B)))))

(deftest remove-jobs-already-assigned-test
  (testing "test remove jobs already assigned"
    (let [jobs-assigned [{"job_assigned" {"job_id" mock-job-id-A, "agent_id" "whatever"}}]]
      (is (= (remove-jobs-already-assigned mock-group-jobs-A jobs-assigned) (list mock-job-B mock-job-C))))))

(deftest create-job-assign-test
  (testing "test create job assign"
    (is (= (create-job-assign mock-job-A mock-agent-A)
          {"job_assigned" {"job_id" mock-job-id-A, "agent_id" mock-agent-id-A}}))))

(deftest assign-jobs-test
  (testing "test assign jobs"
    (is (= (assign-jobs mock-group-jobs-request-A mock-group-agents-A mock-group-jobs-A [])
          [{"job_assigned" {"job_id" mock-job-id-A, "agent_id" mock-agent-id-A}},
           {"job_assigned" {"job_id" mock-job-id-B, "agent_id" mock-agent-id-B}}]))))

)
