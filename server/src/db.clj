(ns db
  (:require [clojure.java.jdbc :as j]
            [clojure.string]))

(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/dupe"
   :user "dupe"
   :password "dupe"})


(def tables '(:users :comments :reports :tasks :tasks_in_reports))

(defn -make-in-str [ids]
  (str "(" (clojure.string/join "," ids) ")"))


(defn truncate-table [table-name]
  (j/execute! db-spec [(str "truncate table " table-name)] :transaction? false))

(defn truncate-all []
  (doall
    (map truncate-table
         (map name tables))))

(defn finalize-previous-report []
  (j/update! db-spec :reports {:finalized true} ["finalized=false limit 1"]))

(defn create-new-report []
  (:generated_key
    (first
      (j/insert! db-spec :reports {:id nil}))))

(defn insert-one-task [task]
  (:generated_key
    (first
      (j/insert! db-spec :tasks task))))

(defn get-ids-for-tasks [tasks]
  (let [missing-id? #(nil? (:id %))
        to-insert (filter missing-id? tasks)
        others (filter (complement missing-id?) tasks)]
    (doall
      (concat (map :id others)
        (map insert-one-task to-insert)))))

(defn insert-report-task-mappings [report-id task-ids planned?]
  (let [insert (partial j/insert! db-spec :tasks_in_reports)
        rows (for [task-id task-ids]
               [{:report_id report-id :task_id task-id :planned planned?}])]
    (map #(apply insert %) rows)))

(defn get-latest-report []
  (first (j/query db-spec ["select * from reports where finalized = false limit 1"])))

(defn get-tasks-for-report [report-id]
  (j/query db-spec ["select * from tasks join tasks_in_reports on tasks.id = task_id where report_id = ?" report-id]))

(defn get-tasks [task-ids]
  (j/query db-spec [(str "select * from tasks where id in " (-make-in-str task-ids))]))

(defn update-task [id done?]
  (j/update! db-spec :tasks {:done done?} ["id=?" id]))

(defn add-comment [task-id comment]
  (j/insert! db-spec :comments {:task_id task-id :comment comment}))

(defn get-comments-for-tasks [task-ids]
  (println task-ids)
  (j/query db-spec [(str "select * from comments where task_id in " (-make-in-str task-ids))]))

(def insert-q
  "insert into users
    (id, access_token, login, name, email, avatar_url)
  values
    (?, ?, ?, ?, ?, ?)
  on duplicate key update
    access_token = values(access_token),
    login = values(login),
    name = values(name),
    email = values(email),
    avatar_url = values(avatar_url)")

(defn insert-or-update-user [user]
  (j/execute! db-spec [insert-q
                       (get user "id")
                       (get user "access_token")
                       (get user "login")
                       (get user "name")
                       (get user "email")
                       (get user "avatar_url")]))

(defn get-user-by-access-token [access-token]
  (first
    (j/query db-spec ["select * from users where access_token = ?" access-token])))
