(ns datasource.reports
  (:require [clojure.java.jdbc :as j]
            [datasource.util]))

(defn reset [db]
  (datasource.util/truncate-table db :reports))

(defn finalize-previous-report [db user-id]
  (j/update! db :reports {:finalized true} ["user_id = ? and finalized=false limit 1" user-id]))

(defn create-new-report [db user-id]
  (:generated_key
    (first
      (j/insert! db :reports {:id nil :user_id user-id}))))

(defn get-latest-report [db user-id]
  (first (j/query db ["select * from reports where user_id = ? and finalized = false limit 1" user-id])))

