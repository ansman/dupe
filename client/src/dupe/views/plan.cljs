(ns dupe.views.plan
  (:require [dommy.core :as dommy]
            [dupe.api :as api]
            [dupe.router :as router]
            [clojure.string :refer [trim]])
  (:require-macros [dommy.macros :refer [deftemplate sel1 sel node]]))

(deftemplate task-input [placeholder]
  [:.form-group.task
   [:input.input-lg.form-control {:type "text" :placeholder placeholder :value placeholder}]])

(deftemplate render []
  [:.page-header
    [:h1 "What are you doing today?"]]
  [:form
   (task-input "Collect underpants")
   (task-input "...")
   (task-input "Profit")
   [:.form-group.form-actions
    [:input.btn.btn-primary.btn-lg {:type "submit" :value "Plan my day"}]]])

(defn latest-fetched [root & {:keys [planned unplanned]}]
  (dommy/replace-contents! root (render)))

(defn add-task-inputs-if-needed [root]
  (when-not (empty? (.-value (last (sel root [:.task :input]))))
    (dommy/insert-before! (task-input "") (sel1 root :.form-actions))))

(defn empty-task-input? [form-group]
  (empty? (.-value (sel1 form-group :input))))

(defn task-inputs-to-remove [root]
  (filter empty-task-input? (nthnext (butlast (sel root :.task)) 3)))

(defn remove-task-input [group]
  (when (.-parentNode group)
    (dommy/remove! group)))

(defn remove-task-inputs-if-needed [root]
  (doall (map remove-task-input (task-inputs-to-remove root))))

(defn update-task-inputs [root ev]
  (add-task-inputs-if-needed root)
  (when-not (empty? (remove-task-inputs-if-needed root))
    (.focus (last (sel root [:.task :input])))))

(defn planned-tasks [root]
  (map (fn [description] {:id nil :description description})
    (filter (complement empty?)
      (map trim
        (map #(.-value %) (sel root [:.task :input]))))))

(defn submit-button [root]
  (sel1 root "input[type=submit]"))

(defn form-enabled [root]
  (not (.-disabled (submit-button root))))

(defn disable-form [root]
  (set! (.-disabled (submit-button root)) true))

(defn submit-plan [root ev]
  (.preventDefault ev)
  (if (form-enabled root)
    (let [planned (planned-tasks root)]
      (when-not (empty? planned)
        (disable-form root)
        (api/post "/planned"
                  :data planned
                  :on-success (fn [data] (router/navigate "")))))))

(defn create-root []
  (let [root (node [:#plan])]
    (dommy/listen! [root :.task]
                   :keyup (partial update-task-inputs root)
                   :paste (partial update-task-inputs root))
    (dommy/listen! [root :form] :submit (partial submit-plan root))
    root))

(defn ^:export show []
  (let [root (create-root)]
    (api/get "/latest" :on-success (partial latest-fetched root))
    root))
