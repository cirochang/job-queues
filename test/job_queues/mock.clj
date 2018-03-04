(ns job-queues.mock)

(def job-id-A "f26e890b-df8e-422e-a39c-7762aa0bac36")
(def job-id-B "690de6bc-163c-4345-bf6f-25dd0c58e864")
(def job-id-C "c0033410-981c-428a-954a-35dec05ef1d2")
(def job-A {"id" job-id-A, "type" "rewards-question", "urgent" false})
(def job-B {"id" job-id-B, "type" "bills-questions", "urgent" false})
(def job-C {"id" job-id-C, "type" "bills-questions", "urgent" true})

(def agent-id-A "8ab86c18-3fae-4804-bfd9-c3d6e8f66260")
(def agent-id-B "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")
(def agent-A {"id" agent-id-A, "name" "BoJack Horseman", "primary_skillset" ["bills-questions"], "secondary_skillset" []})
(def agent-B {"id" agent-id-B, "name" "Mr. Peanut Butter", "primary_skillset" ["rewards-question"], "secondary_skillset" ["bills-questions"]})
