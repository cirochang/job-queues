(ns job-queues.parsers-test
    (:require [clojure.test :refer :all]
              [job-queues.parsers :refer :all]))

(def mock-input 
  [{"new_agent" {}}
   {"new_job" {"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false}}
   {"new_agent" {}} 
   {"new_job" {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false}}
   {"new_job" {"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true}}
   {"job_request" {}}
   {"job_request" {}}])

(deftest filter-input-by-entity-test
  (testing "test filter input by entity"
    (is (= (filter-input-by-entity mock-input  "new_job")
          [{"new_job" {"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false}}
           {"new_job" {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false}}
           {"new_job" {"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true}}]))))

(deftest chunk-input-by-entity-test
  (testing "test chunk input by entity"
    (is (= (chunk-input-by-entity mock-input  "new_job")
          [{"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false}
           {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false}
           {"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true}]))))
