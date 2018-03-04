(defproject job-queues "2.0.0"
  :description "An exercise of functional programming provided by Nubank."
  :url "https://gitlab.com/cirochang/job-queues/tree/organizing-repository"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-json "0.1.2"]
                 [compojure "1.3.4"]
                 [com.novemberain/monger "3.1.0"]
                 [cheshire "5.1.1"]
                 [prismatic/schema "1.1.7"]]
  :plugins [[lein-ring "0.12.3"]]
  :ring {:handler job-queues.handler/app}
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]
                                  [ring/ring-core "1.6.3"]
                                  [javax.servlet/servlet-api "2.5"]]}})
