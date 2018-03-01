(ns job-queues.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.data.json :as json]
            [job-queues.routes :refer [main-routes]]
            [job-queues.control :as control]
            [job-queues.parsers :as parsers]))

(defn stream-to-str
  "Convert a stream to string"
  [stream]
  (reduce str (line-seq (java.io.BufferedReader. stream))))

(defn -dev-main
  "A very simple web server using Ring & Jetty that reloads code changes via the development profile of Leiningen"
  [port-number]
  (jetty/run-jetty (wrap-reload #'main-routes)
    {:port (Integer. port-number)}))

(defn -main
  "A very simple web server using Ring & Jetty"
  [port-number]
  (jetty/run-jetty main-routes
    {:port (Integer. port-number)}))

(comment
  (defn -main
    "Read a json input and output a json with jobs assigned"
    []
    (let [input (json/read-str (stream-to-str *in*))
          jobs (parsers/chunk-input-by-entity input "new_job")
          agents (parsers/chunk-input-by-entity input "new_agent")
          jobs-request (parsers/chunk-input-by-entity input "job_request")
          jobs-assigned (control/assign-jobs jobs-request agents jobs [])]
      (println (json/write-str jobs-assigned))))
)
