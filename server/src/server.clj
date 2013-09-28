(ns server
  (:use [org.httpkit.server :only [run-server]])
  (:require [routes]))

(defn app [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (run-server
            (routes/app) {:port 8080
                          :thread 4})))

(defn -main
  [& args]
  (start-server)
  (println "up!"))
