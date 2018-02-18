(ns job-queues.control)

(defn get-agent-by-id
  "Get the agent by id"
  [agents agent-id]
  (first (filter #(= (get % "id") agent-id) agents)))

(defn get-jobs-by-type
  "Get jobs by type"
  [jobs type]
  (filter #(>= (.indexOf type (get % "type")) 0) jobs))

(defn order-jobs-by-urgent
  "Order jobs with urgent jobs in the beggining of sequence"
  [jobs]
  (let [splited-jobs (group-by #(get % "urgent") jobs)]
    (concat (get splited-jobs true)
      (get splited-jobs false))))

(defn get-possible-jobs-by-agent
  "Get all possible jobs for some agent ordered by skillsets"
  [jobs agent]
  (let [primary-skillset (get agent "primary_skillset")
        secondary-skillset (get agent "secondary_skillset")
        primary-jobs (order-jobs-by-urgent (get-jobs-by-type jobs primary-skillset))
        secondary-jobs (order-jobs-by-urgent (get-jobs-by-type jobs secondary-skillset))]
    (distinct (concat primary-jobs secondary-jobs))))

(defn remove-jobs-already-assigned
  "Remove jobs that already assigned"
  [jobs jobs-assigned]
  (let [id-assigned (map #(get-in % ["job_assigned" "job_id"]) jobs-assigned)]
    (remove #(>= (.indexOf id-assigned (get % "id")) 0) jobs)))

(defn create-job-assign
  "Create a new job assign"
  [job agent]
  {"job_assigned" {"job_id" (get job "id"), "agent_id" (get agent "id")}})

(defn assign-jobs
  "For each agent assign a job and return the jobs assigned"
  [jobs-request agents jobs jobs-assigned]
  (if (seq jobs-request)
    (let [agent (get-agent-by-id agents (get (first jobs-request) "agent_id"))
          possible-jobs (get-possible-jobs-by-agent jobs agent)
          best-job (first (remove-jobs-already-assigned possible-jobs jobs-assigned))
          updated-jobs-assigned (conj jobs-assigned (create-job-assign best-job agent))]
      (assign-jobs (rest jobs-request) agents jobs updated-jobs-assigned))
    jobs-assigned))