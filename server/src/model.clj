(ns model
  (:require [db]))

(defn init []
  (println "init model")
  (db/truncate-all))

(defn new-report [planned]
  (db/finalize-previous-report)
  (let [report-id (db/create-new-report)
        task-ids (db/get-ids-for-tasks planned)]
    (db/insert-report-task-mappings report-id task-ids true)
  ))

(defn update-report [unplanned]
  (let [report-id (:id (db/get-latest-report))
        task-ids (db/get-ids-for-tasks unplanned)]
    (db/insert-report-task-mappings report-id task-ids false)
  ))

(defn update-task [id done?]
  (db/update-task id done?))

(defn add-task-comment [task-id comment]
  (db/add-comment task-id comment))

(defn clean-task [task]
  (select-keys task [:id :description :done :comments]))

(defn sort-tasks [tasks]
  (let [f #(map clean-task (filter % tasks))]
    {:planned (doall (f :planned))
     :unplanned (doall (f (complement :planned)))}))

(defn -clean-comment [comment]
  (select-keys comment [:id :comment]))

(defn get-comment-lookup-for-tasks [tasks]
  (println tasks)
  (let [task-ids (distinct (map :id tasks))]
    (group-by :task_id (db/get-comments-for-tasks task-ids))
  ))

(defn get-and-add-comments-to-tasks [tasks]
  (if (empty? tasks)
    tasks
    (let [task-lookup (get-comment-lookup-for-tasks tasks)]
      (map (fn [t] (assoc t :comments
                          (map -clean-comment
                               (get task-lookup (:id t))))) tasks))))

(defn get-latest-report []
  (let [latest-report (db/get-latest-report)]
    (if (nil? latest-report)
      {:planned ()
       :unplanned ()}
      (doall (-> latest-report
        :id
        db/get-tasks-for-report
        get-and-add-comments-to-tasks
        sort-tasks)))))
