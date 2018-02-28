(defproject job-queues "2.0.0"
  :description "An exercise of functional programming provided by Nubank."
  :url "https://gitlab.com/cirochang/job-queues/tree/organizing-repository"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring "1.4.0"]]
  :main ^:skip-aot job-queues.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
