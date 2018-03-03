(ns job-queues.schemas
  (:require [schema.core :refer [required-key Str Bool]]))

(def job
  "A schema for job creation."
  {(required-key "id") Str
   (required-key "type") Str
   (required-key "urgent") Bool})

(def agent
  "A schema for agent creation."
  {(required-key "id") Str
   (required-key "name") Str
   (required-key "primary_skillset") [Str]
   (required-key "secondary_skillset") [Str]})

(def request-job
  "A schema for request-job assign."
   {(required-key "agent_id") Str})
