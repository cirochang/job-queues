(ns job-queues.fixture
  (:require [monger.core :as mg]
            [job-queues.database :refer [create-db destroy-db]]))

(defn database-test-fixture
  "Use a test database for the tests."
  [f]
  (with-redefs [job-queues.database/db (mg/get-db (mg/connect) "job-queues-test")]
    (destroy-db)
    (create-db)
    (f)
    (destroy-db)))
