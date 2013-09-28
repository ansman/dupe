(ns model)

(def data (atom []))
(def counter (atom 0))

(defn init []
  (println "init model")
  (reset! data [])
  (reset! counter 0))

(defn next-id []
  (swap! counter inc))

(defn -new-task [planned?]
  {:id (next-id)
   :done false
   :comments []
   :planned planned?})

(defn -planned? [task]
  (:planned task))

(defn -new-task-and-update [planned?]
  (fn [changes]
    (merge (-new-task planned?) changes)))

(defn -clean-task [task]
  (dissoc task :planned))

(defn -get-cleaned-tasks [planned?]
  (let [f (if planned? -planned? (complement -planned?))]
    (map -clean-task (filter f @data))))

(defn -has-id? [id]
  (fn [task] (= (:id task) id)))

(defn -get-task [id]
  (first (filter (-has-id? id) @data)))

(defn -del-task [id]
  (swap! data (partial remove (-has-id? id))))

(defn -add-task [task]
  (swap! data #(conj % task)))

(defn new-report [planned]
  (swap! data (fn [x] (map (-new-task-and-update true) planned))))

(defn update-report [unplanned]
  (swap! data #(concat % (map (-new-task-and-update false) unplanned))))

(defn update-task [id done?]
  (let [task (-get-task id)]
    (-del-task id)
    (-add-task (assoc task :done done?))))

(defn add-task-comment [id comment]
  (let [task (-get-task id)]
    (-del-task id)
    (-add-task (update-in task [:comments] conj comment))))

(defn get-latest-report []
  {:planned (-get-cleaned-tasks true)
   :unplanned (-get-cleaned-tasks false)})
