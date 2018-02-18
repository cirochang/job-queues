(ns job-queues.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [job-queues.utils :as utils]))

(defn split-input
  "Split input"
  [input entity]
  (filter #(= (first (keys %)) entity)
    input))

(defn parse-input
  "Parse input"
  [input entity]
  (map #(get % entity)
    (split-input input entity)))

(defn order-jobs-by-urgent
  "Order jobs elements by urgent true"; doc: Put urgent jobs in the beggining of sequence
  [jobs]
  (let [splited-jobs (group-by #(get % "urgent") jobs)]
    (concat (get splited-jobs true)
      (get splited-jobs false))))

(defn get-agent-by-id
  "Get agent proprieties by id"
  [agent-id agents]
  (first (filter #(= (get % "id") agent-id) agents)))

(defn get-jobs-by-type ;; change type to skillset 
  "Get jobs by skillset"
  [jobs skillset]
  (filter #(>= (.indexOf skillset (get % "type")) 0) jobs))

(defn get-best-jobs-by-agent ;; change this function name
  "Seach the bests jobs for some agent"
  [jobs agent]
  (let [primary_skillset (get agent "primary_skillset")
        secondary_skillset (get agent "secondary_skillset")
        primary-bests-jobs (get-jobs-by-type jobs primary_skillset)
        secondary-bests-jobs (get-jobs-by-type jobs secondary_skillset)]
    (distinct (concat primary-bests-jobs secondary-bests-jobs))))

(defn remove-jobs-already-assigned
  "Remove jobs that already assigned"
  [best-jobs jobs_assigned]
  (let [id_assigned (map #(get (get % "job_assigned") "job_id") jobs_assigned)]
    (remove #(>= (.indexOf id_assigned (get % "id")) 0) best-jobs)))

(defn assign-jobs
  "Assign Jobs to Agent"
  [job_request agents jobs jobs_assigned]
  (if (seq job_request)
    (let [agent (get-agent-by-id (get (first job_request) "agent_id") agents)
          best-jobs (get-best-jobs-by-agent jobs agent) ;; change to possible jobs
          selected-job (first (remove-jobs-already-assigned best-jobs jobs_assigned)) ;; change to best-job
          new_job_assigned {"job_assigned" {"job_id" (get selected-job "id"), "agent_id" (get agent "id")}}]
      (assign-jobs (rest job_request) agents jobs (conj jobs_assigned new_job_assigned) ))
    jobs_assigned))


(defn -main
  "Read json from stdin and " ;; change this doc
  []
  (let [input (json/read-str (utils/stream-to-str *in*))
        jobs (order-jobs-by-urgent (parse-input input "new_job"))
        agents (parse-input input "new_agent")
        job_request (parse-input input "job_request")
        jobs_assigned (assign-jobs job_request agents jobs [])]
    (println (json/write-str jobs_assigned))))
