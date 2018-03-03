(ns job-queues.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [compojure.core :refer [defroutes POST GET]]
            [job-queues.controller :as controller]))

(defroutes app-routes
  (POST "/agents" {body :body} (controller/create-agent body))
  (POST "/jobs" {body :body} (controller/create-job body))
  (POST "/request_jobs" {body :body} (controller/assign-job body))
  (GET "/queue_state" [] (controller/get-queue-state))
  (GET "/agent_stats/:agent-id" [agent-id] (controller/get-agent-stats agent-id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)))
