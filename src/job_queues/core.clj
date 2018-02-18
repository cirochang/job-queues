(ns job-queues.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [job-queues.control :as control]
            [job-queues.parsers :as parsers]))

(defn stream-to-str
  "Convert a stream to string"
  [stream]
  (reduce str (line-seq (java.io.BufferedReader. stream))))

(defn -main
  "Read a json input and output a json with jobs assigned"
  []
  (let [input (json/read-str (stream-to-str *in*))
        jobs (parsers/chunk-input-by-entity input "new_job")
        agents (parsers/chunk-input-by-entity input "new_agent")
        jobs-request (parsers/chunk-input-by-entity input "job_request")
        jobs-assigned (control/assign-jobs jobs-request agents jobs [])]
    (println (json/write-str jobs-assigned))))
