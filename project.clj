(defproject job-queues "2.0.0"
  :description "An exercise of functional programming provided by Nubank."
  :url "https://gitlab.com/cirochang/job-queues/tree/organizing-repository"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring "1.4.0"]
                 [compojure "1.3.4"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :main ^:skip-aot job-queues.core
  :target-path "target/%s"
  :profiles {:uberjar
              {:aot :all}
             :dev
               {:main job-queues.core/-dev-main}})
