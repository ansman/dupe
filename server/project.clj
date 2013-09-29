(defproject server "0.1.0-SNAPSHOT"
  :description "TODO"
  :url "TODO"
  :resource-paths ["templates"]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [org.clojure/data.codec "0.1.0"]
                 [http-kit "2.1.10"]
                 [ring/ring-core "1.2.0"]
                 [compojure "1.1.5" :exclusions [ring/ring-core]]
                 [me.shenfeng/mustache "1.1"]
                 [org.clojure/data.json "0.2.3"]
                 [midje "1.5.1"]
                 [log4j/log4j "1.2.17"]
                 [ring.middleware.logger "0.4.0"]
                 [http-kit/dbcp "0.1.0"]
                 [mysql/mysql-connector-java "5.1.21"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]]
                   :source-paths ["dev"]}}
  :uberjar-name "server-standalone.jar"
  :main main
  :aot [main])
