(ns job-queues.controller
  (:require [ring.util.response :refer [response]]
            [schema.core :refer [validate]]
            [job-queues.schemas :as schemas]
            [job-queues.database :as database]))

(defn create-job
  "Add a job in database and return 200"
  [job]
  (try
    (do
      (validate schemas/job job)
      (database/insert-job job)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (response "This job already exists in database."))
    (catch java.lang.RuntimeException e
      (response (str (.getMessage e))))))

(defn create-agent
  "Add an agent in database and return 200"
  [agent]
  (try
    (do
      (validate schemas/agent agent)
      (database/insert-agent agent)
      (response "ok"))
    (catch com.mongodb.DuplicateKeyException e
      (response "This agent already exists in database."))
    (catch java.lang.RuntimeException e
      (response (str (.getMessage e))))))

(defn assign-job
  "Assign an agent-id to a job.
   And return the id of this job or indicate the lack of one."
  [request-job]
  (try
    (do
      (validate schemas/request-job request-job)
      (let [agent-id (get request-job "agent_id")
            agent (database/get-agent-by-id agent-id)
            primary-job (database/get-best-job-by-skillsets (get agent :primary_skillset))
            secondary-job (database/get-best-job-by-skillsets (get agent :secondary_skillset))
            best-job (first (concat primary-job secondary-job))]
        (database/update-job-assigned (:id best-job) agent-id)
        (response {"job_id" (:id best-job) "agent_id" agent-id})))
    (catch java.lang.RuntimeException e
      (response (str (.getMessage e))))))

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
