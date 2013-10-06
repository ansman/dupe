(ns model
  (:require [datasource.core]
            [datasource.users]
            [datasource.reports]
            [datasource.tasks]))

(defn init [system]
  (datasource.core/reset-all (:db system)))

(defn -clean-task [task]
  (select-keys task [:id :description :done :comments]))

(defn new-report [system user-id planned]
  (let [db (:db system)]
    (datasource.reports/finalize-previous-report db user-id)
    (let [report-id (datasource.reports/create-new-report db user-id)
          task-ids (datasource.tasks/get-ids-for-tasks db planned)]
      (doall
        (datasource.tasks/insert-report-task-mappings db report-id task-ids true))
    )))

(defn update-report [system user-id unplanned]
  (let [db (:db system)
        report-id (:id (datasource.reports/get-latest-report db user-id))
        task-ids (datasource.tasks/get-ids-for-tasks db unplanned)]
    (doall
      (datasource.tasks/insert-report-task-mappings db report-id task-ids false))
    (doall (map -clean-task (datasource.tasks/get-tasks db task-ids)))
  ))

(defn update-task [system id done?]
  (datasource.tasks/update-task (:db system) id done?))

(defn add-task-comment [system task-id comment]
  (datasource.tasks/add-comment (:db system) task-id comment))

(defn -sort-tasks [tasks]
  (let [f #(map -clean-task (filter % tasks))]
    {:planned (doall (f :planned))
     :unplanned (doall (f (complement :planned)))}))

(defn -clean-comment [comment]
  (select-keys comment [:id :comment]))

(defn get-comment-lookup-for-tasks [system tasks]
  (let [task-ids (distinct (map :id tasks))]
    (group-by :task_id (datasource.tasks/get-comments-for-tasks (:db system) task-ids))
  ))

(defn get-and-add-comments-to-tasks [system tasks]
  (if (empty? tasks)
    tasks
    (let [task-lookup (get-comment-lookup-for-tasks system tasks)]
      (map (fn [t] (assoc t :comments
                          (map -clean-comment
                               (get task-lookup (:id t))))) tasks))))

(defn get-latest-report [system user-id]
  (let [db (:db system)
        latest-report (datasource.reports/get-latest-report db user-id)]
    (if (nil? latest-report)
      {:planned ()
       :unplanned ()}
      (doall (->> latest-report
        :id
        (datasource.tasks/get-tasks-for-report db)
        (get-and-add-comments-to-tasks system)
        -sort-tasks)))))
