(ns job-queues.parsers)

(defn filter-input-by-entity
  "Filter the input by entity"
  [input entity]
  (filter #(= (first (keys %)) entity)
    input))

(defn chunk-input-by-entity
  "Chunk the input by entity"
  [input entity]
  (map #(get % entity)
    (filter-input-by-entity input entity)))
