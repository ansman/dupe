(defproject server "0.1.0-SNAPSHOT"
  :description "TODO"
  :url "TODO"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]]
                   :source-paths ["dev"]}}
  :main server)