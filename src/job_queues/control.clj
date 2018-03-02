(ns job-queues.control
  (:require [ring.util.response :refer [response]]
            [job-queues.util.database :as database]))

(defn create-job
  "Add a job in database and return 200"
  [job]
  (try
    (do
      (database/insert-job job)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (response "This job already exists in database."))))

(defn create-agent
  "Add an agent in database and return 200"
  [agent]
  (try
    (do
      (database/insert-agent agent)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (response "This agent already exists in database."))))

(defn assign-job
  "Assign an agent-id to a job.
   And return the id of this job or indicate the lack of one."
  [agent-id]
  (let [agent (database/get-agent-by-id agent-id)
        primary-job (database/get-best-job-by-skillsets (get agent :primary_skillset))
        secondary-job (database/get-best-job-by-skillsets (get agent :secondary_skillset))
        best-job (first (concat primary-job secondary-job))]
    (if (:id best-job) (database/update-job-assigned (:id best-job) agent-id))
    (response {"job_id" (:id best-job) "agent_id" agent-id})))
