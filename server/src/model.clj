(ns model)

(def data (atom {:planned []
                 :unplanned []}))

(def counter (atom 0))

(defn next-id []
  (swap! counter inc))

(defn -init-task [task]
  (let [id (:id task)]
    (assoc task :id (or id (next-id))
                :done false
                :comments [])))

(defn new-report [planned]
  (let [planned (map -init-task planned)]
    (swap! data (fn [prev] {:planned planned
                            :unplanned []}))))

(defn update-report [unplanned]
  (let [unplanned (map -init-task unplanned)]
    (swap! data (fn [prev] {:planned (:planned prev)
                            :unplanned (concat unplanned (:unplanned prev))}))))

(defn update-task [id done?])
(defn add-task-comment [comment])

(defn get-latest-report []
  @data)
