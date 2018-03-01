(ns job-queues.utils.database
  (:require [clojure.java.jdbc :as sql]))

(def db {:classname "org.sqlite.JDBC", :subprotocol "sqlite", :subname "test.db"})

(def job-schema [[:id "varchar(32)" :primary :key][:type "varchar(32)"][:urgent :boolean]])
(def agent-schema [[:id "varchar(32)" :primary :key][:type "varchar(32)"][:urgent :boolean]])
(def agent-id-schema [[:id "varchar(32)" :primary :key][:type "varchar(32)"][:urgent :boolean]])

(defn create-database
  []
  (let [create-job-table (sql/create-table-ddl :job job-schema)
       [create-]
  (sql/execute! db [create-job-table])))
