(ns dupe.views.followup,
  (:require [dommy.core :as dommy]
            [dupe.api :as api]
            [dupe.router :as router])
  (:require-macros [dommy.macros :refer [node deftemplate sel1 sel]]))

(deftemplate render-planned-task [task]
   [:.form-group.planned-task {:data-id (:id task)}
    [:label.checkbox-label
     [:input {:type "checkbox" :checked (:done task)}]
     [:span.text (:description task)]]])

(deftemplate render-unplanned-task [task]
  [:div])

(deftemplate render [planned unplanned]
  [:.page-header
    [:h2 "Todays tasks"]]
  [:form.row
   [:.col-md-6.col-sm-12
    [:h2 "Planned tasks"]
    [:#planned-tasks
     (map render-planned-task planned)]]
   [:.col-md-6.col-sm-12
    [:h2 "Unplanned tasks"]
    [:#unplanned-tasks
     (map render-unplanned-task unplanned)]
    [:.row
     [:.col-md-10
      [:input.input-lg {:type "text"}]]
     [:.col-md-2
      [:button.add-unplanned-task.btn.btn-default "Add"]]]]])


(defn followup-fetched [root {:keys [planned unplanned]}]
  (if (empty? planned)
    (router/navigate "plan")
    (dommy/replace-contents! root (render planned unplanned))))

(defn toggle-task-input [ev]
  (let [id (.-dataset.id (dommy/closest (.-target ev) :.planned-task))]
    (api/put (str "/tasks/" id)
             :data {:done (.-checked (.-target ev))})))

(defn create-root []
  (let [root (node [:#followup])]
    (dommy/listen! [root [:.planned-task :input]]
                   :change toggle-task-input)
    ;                :paste (partial update-task-inputs root))
    ; (dommy/listen! [root :form] :submit (partial submit-plan root))
    root))

(defn ^:export show []
  (let [root (create-root)]
    (api/get "/latest" :on-success (partial followup-fetched root))
    root))
