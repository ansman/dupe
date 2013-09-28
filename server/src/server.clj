(ns server
  (:gen-class)
  (:use [org.httpkit.server :only [run-server]])
  (:require [routes]))

(defonce server (atom nil))

(defn start []
  (reset! server
          (run-server
            (routes/app) {:port 8080
                          :thread 4})))

(defn stop []
  (@server))
