(ns datasource.util
  (:require [clojure.java.jdbc :as j]))

(defn truncate-table [db table]
  (j/execute! db [(str "truncate table " (name table))] :transaction? false))

