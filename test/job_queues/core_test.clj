(ns job-queues.core-test
  (:require [clojure.test :refer :all]
            [job-queues.core :refer :all]))

(def mock-jobs
  [{"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true},
   {"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false},
   {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false}])

(def mock-agents
 '({"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260", "name" "BoJack Horseman", "primary_skillset" ["bills-questions"], "secondary_skillset" []},
   {"id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88", "name" "Mr. Peanut Butter", "primary_skillset" ["rewards-question"], "secondary_skillset" ["bills-questions"]}))

(deftest get-jobs-by-type-test
  (testing "check if get correct jobs"
    (is (= (get-jobs-by-type mock-jobs ["bills-questions"])
           '({"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true},
             {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false})))
    (is (= (get-jobs-by-type mock-jobs ["rewards-question"])
           '({"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false})))
  ))

(deftest get-best-jobs-by-agent-test
  (testing "check if get the possible jobs"
    (is (= (get-best-jobs-by-agent mock-jobs (first mock-agents))
           '({"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true},
             {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false})))
    (is (= (get-best-jobs-by-agent mock-jobs (last mock-agents))
           '({"id" "f26e890b-df8e-422e-a39c-7762aa0bac36", "type" "rewards-question", "urgent" false},
             {"id" "c0033410-981c-428a-954a-35dec05ef1d2", "type" "bills-questions", "urgent" true},
             {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864", "type" "bills-questions", "urgent" false})))
  ))
