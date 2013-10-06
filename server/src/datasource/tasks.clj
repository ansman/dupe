(ns datasource.tasks
  (:require [clojure.java.jdbc :as j]
            [clojure.string]
            [datasource.util]))


(defn reset [db]
  (datasource.util/truncate-table db :tasks)
  (datasource.util/truncate-table db :tasks_in_reports)
  (datasource.util/truncate-table db :comments))


(defn -make-in-str [ids]
  (str "(" (clojure.string/join "," ids) ")"))


(defn insert-one-task [db task]
  (:generated_key
    (first
      (j/insert! db :tasks task))))

(defn get-ids-for-tasks [db tasks]
  (let [missing-id? #(nil? (:id %))
        to-insert (filter missing-id? tasks)
        others (filter (complement missing-id?) tasks)]
    (doall
      (concat (map :id others)
        (map (partial insert-one-task db) to-insert)))))

(defn insert-report-task-mappings [db report-id task-ids planned?]
  (let [insert (partial j/insert! db :tasks_in_reports)
        rows (for [task-id task-ids]
               [{:report_id report-id :task_id task-id :planned planned?}])]
    (map #(apply insert %) rows)))

(defn get-tasks-for-report [db report-id]
  (j/query db ["select * from tasks join tasks_in_reports on tasks.id = task_id where report_id = ?" report-id]))

(defn get-tasks [db task-ids]
  (j/query db [(str "select * from tasks where id in " (-make-in-str task-ids))]))

(defn update-task [db id done?]
  (j/update! db :tasks {:done done?} ["id=?" id]))

(defn add-comment [db task-id comment]
  (j/insert! db :comments {:task_id task-id :comment comment}))

(defn get-comments-for-tasks [db task-ids]
  (j/query db [(str "select * from comments where task_id in " (-make-in-str task-ids))]))
