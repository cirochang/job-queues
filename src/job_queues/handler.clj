(ns job-queues.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [job-queues.control :as control]
            [compojure.core :refer [defroutes POST GET]]))

(defroutes app-routes
  (POST "/agents" {body :body} (control/create-agent body))
  (POST "/jobs" {body :body} (control/create-job body))
  (POST "/request_jobs" {body :body} (control/assign-job (get body "agent_id")))
  (GET "/current_queue_state" [] control/create-job)
  (GET "/agent_stats" [] control/create-job)
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)))
