(ns job-queues.request.helpper
  (:require [ring.mock.request :as req-mock]
            [job-queues.handler :refer [app]]))

(defn do-request
  "Helpper to do some requests and populate the database."
  [request-type endpoint body]
  (app (req-mock/json-body (req-mock/request request-type endpoint) body)))
