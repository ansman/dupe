(ns main
  (:gen-class)
  (:require [server]))

(defn -main
  [& args]
  (server/start)
  (println "up!"))
