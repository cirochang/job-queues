(ns job-queues.routes
  (:require [compojure.core :refer [defroutes POST GET]]
            [job-queues.utils.database :refer [create-database]]))

(defn welcome
  [request]
  (create-database)
  {:status 200
   :body "oi"
   :headers {}})

(defroutes main-routes
  (POST "/agents" [] welcome)
  (POST "/jobs" [] welcome)
  (POST "/request_jobs" [] welcome)
  (GET "/current_queue_state" [] welcome)
  (GET "/agent_stats" [] welcome))
