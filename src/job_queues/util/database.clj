(ns job-queues.util.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer [with-collection find sort limit fields]]
            [monger.operators :refer [$in $set]])
  (:import org.bson.types.ObjectId))

(def db (mg/get-db (mg/connect) "job-queues"))

(mc/create-index db "jobs" ["id" "assigned_by_agent"] {:unique true})
(mc/create-index db "agents" ["id"] {:unique true})

(defn insert-job
  "Insert a job to mongodb with default status waiting."
  [job]
  (mc/insert db "jobs" (merge job {"status" "waiting" "created_at" (java.util.Date.)})))

(defn insert-agent
  "Insert an agent to mongodb."
  [agent]
  (mc/insert db "agents" (merge agent {"created_at" (java.util.Date.)})))

(defn get-agent-by-id
  [agent-id]
  (mc/find-one-as-map db "agents" {"id" agent-id}))

(defn get-best-job-by-skillsets
  [skillsets]
  (with-collection db "jobs"
      (find {:type {$in skillsets} :status "waiting"})
      (sort {:urgent -1 :created_at 1})
      (limit 1)))

(defn update-job-assigned
  [job-id agent-id]
  (mc/update db "jobs" {"status" "being done" "assigned_by_agent" agent-id} {$set {"status" "completed"}})
  (mc/update db "jobs" {"id" job-id} {$set {"assigned_by_agent" agent-id "status" "being done"}}))
