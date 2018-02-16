(ns job-queues.utils
  (:require [clojure.data.json :as json]))

(defn stream-to-str
  "Convert character input stream to string"
  [input]
  (reduce str (line-seq (java.io.BufferedReader. input))))
