(ns job-queues.controller
  (:require [ring.util.response :refer [response status]]
            [schema.core :refer [validate]]
            [job-queues.schema :as schema]
            [job-queues.database :as database]))

(defn create-job
  "Add a job in database and return 200"
  [job]
  (try
    (do
      (validate schema/job job)
      (database/insert-job job)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (status (response "This job already exists in database.") 409))
    (catch java.lang.RuntimeException e
      (status (response (str (.getMessage e))) 400))))

(defn create-agent
  "Add an agent in database and return 200"
  [agent]
  (try
    (do
      (validate schema/agent agent)
      (database/insert-agent agent)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (status (response "This agent already exists in database.") 409))
    (catch java.lang.RuntimeException e
      (status (response (str (.getMessage e))) 400))))

(defn assign-job
  "Complete the last job assigned by agent if this job exists.
  Assign an agent-id to a best job match if this match exists.
  And return the id of this job or indicate the lack of one (null)."
  [request-job]
  (try
    (do
      (validate schema/request-job request-job)
      (let [agent (database/get-agent-by-id (get request-job "agent_id"))]
        (if agent
          (let [primary-job (database/get-best-job-by-skillsets (:primary_skillset agent))
                secondary-job (database/get-best-job-by-skillsets (:secondary_skillset agent))
                best-job (first (concat primary-job secondary-job))]
            (database/complete-job-by-agent (:id agent))
            (if best-job (database/assign-job-to-agent (:id best-job) (:id agent)))
            (response {"job_id" (:id best-job) "agent_id" (:id agent)}))
          (status (response "Agent not found") 404))))
    (catch java.lang.RuntimeException e
       (status (response (str (.getMessage e))) 400))))

(defn get-queue-state
  "Output a breakdown of the job queue.
  Consisting of all being done, completed, and waiting jobs."
  []
  (let [jobs (database/get-all-jobs)
        jobs-grouped (group-by :status jobs)]
    (response jobs-grouped)))

(defn get-agent-stats
  "Given an agent, output how many jobs of each type this agent has performed."
  [agent-id]
  (let [jobs (database/get-all-jobs-assigned-by-agent agent-id)
        num-jobs-by-type (frequencies (map :type jobs))]
    (response num-jobs-by-type)))
