(ns job-queues.database
  (:require monger.json
            [monger.db :refer [drop-db]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as query]
            [monger.operators :refer [$in $set]]))

(def db (mg/get-db (mg/connect) "job-queues"))

(defn create-db
  "Create some index and unique keys to db."
  []
  (mc/create-index db "jobs" ["id"] {:unique true})
  (mc/create-index db "jobs" ["type" "status" "assigned_by_agent"])
  (mc/create-index db "agents" ["id"] {:unique true}))

(defn destroy-db
  "Destroy database."
  []
  (drop-db db))

(defn clean-db
  "Remove all documents from database."
  []
  (mc/remove db "jobs")
  (mc/remove db "agents"))

(defn insert-job
  "Insert a job to db with default status waiting."
  [job]
  (mc/insert db "jobs" (merge job {"status" "waiting" "created_at" (java.util.Date.)})))

(defn insert-agent
  "Insert an agent to db."
  [agent]
  (mc/insert db "agents" (merge agent {"created_at" (java.util.Date.)})))

(defn get-agent-by-id
  "Get an agent by id"
  [agent-id]
  (mc/find-one-as-map db "agents" {"id" agent-id}))

(defn get-best-job-by-skillsets
  "Get the best job by skillsets"
  [skillsets]
  (query/with-collection db "jobs"
    (query/find {:type {$in skillsets} :status "waiting"})
    (query/sort {:urgent -1 :created_at 1})
    (query/limit 1)))

(defn complete-job-by-agent
  "Update the last job was being done to complete."
  [agent-id]
  (mc/update db "jobs"
    {"status" "being done" "assigned_by_agent" agent-id}
    {$set {"status" "completed"}}))

(defn assign-job-to-agent
  "Assign job to agent and update job status to being done."
  [job-id agent-id]
  (mc/update db "jobs"
    {"id" job-id}
    {$set {"assigned_by_agent" agent-id "status" "being done"}}))

(defn get-all-jobs
  "Get all jobs from db."
  []
  (mc/find-maps db "jobs"))

(defn get-all-jobs-assigned-by-agent
  "Get all jobs that was assigned by an agent."
  [agent-id]
  (mc/find-maps db "jobs" {"assigned_by_agent" agent-id}))
