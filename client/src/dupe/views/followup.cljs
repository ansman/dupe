(ns dupe.views.followup,
  (:require [dommy.core :as dommy]
            [clojure.string :refer [trim]]
            [dupe.api :as api]
            [dupe.router :as router])
  (:require-macros [dommy.macros :refer [node deftemplate sel1 sel]]))

(defn next-page
  ([] (next-page nil))
  ([ev]
   (when ev (.preventDefault ev))
   (router/navigate "plan")))

(def active-request (atom 0))

(defn disable-next-button [root disabled]
  (set! (.-disabled (sel1 root :button.next)) disabled))

(defn start-request [root]
  (swap! active-request inc)
  (disable-next-button root true))

(defn end-request [root]
  (disable-next-button root (swap! active-request dec)))

(deftemplate render-planned-task [task]
  [:.form-group.planned.task {:data-id (:id task)}
   [:label.checkbox-label
    [:input {:type "checkbox" :checked (:done task)}]
    [:span.text (:description task)]]])

(deftemplate render-unplanned-task [task]
  [:.form-group.unplanned.task {:data-id (:id task)}
   [:label.checkbox-label
    [:input {:type "checkbox" :checked (:done task)}]
    [:span.text (:description task)]]])

(deftemplate render [planned unplanned]
  [:.page-header
    [:h2 "Todays tasks"]]
  [:.row
   [:.col-md-6.col-sm-12
    [:h2 "Planned tasks"]
    [:#planned-tasks
     (map render-planned-task planned)]]
   [:.col-md-6.col-sm-12
    [:h2 "Unplanned tasks"]
    [:#unplanned-tasks
     (map render-unplanned-task unplanned)]
    [:form#new-unplanned-task
     [:.input-group
      [:input.form-control {:type "text"}]
      [:span.input-group-btn
       [:input.btn.btn-default {:type "submit" :value "Add"}]]]]]]
  [:button.btn.btn-lg.btn-primary.next "Plan next day"])


(defn followup-fetched [root {:keys [planned unplanned]}]
  (if (empty? planned)
    (next-page)
    (dommy/replace-contents! root (render planned unplanned))))

(defn toggle-task-input [ev]
  (let [input (.-target ev)]
    (let [root (dommy/closest input :#followup)
          id (.-dataset.id (dommy/closest input :.task))]
      (start-request root)
      (api/put (str "/tasks/" id)
               :data {:done (.-checked (.-target ev))}
               :on-success (fn [d] (end-request root))))))

(defn set-unplanned-task-form-state [form disabled]
  (doseq [el (sel form :input)]
    (set! (.-disabled el) disabled)))

(defn setup-new-unplanned-task [new-task-form]
  (let [input (sel1 new-task-form "input[type=text]")]
    (dommy/set-value! input "")
    (.focus input)))

(defn task-added [root tasks]
  (set-unplanned-task-form-state (sel1 root :#new-unplanned-task) false)
  (setup-new-unplanned-task (sel1 root :#new-unplanned-task))
  (dommy/append! (sel1 root :#unplanned-tasks)
                 (map render-unplanned-task tasks))
  (end-request root))

(defn read-new-task [new-task-form]
  (let [input (sel1 new-task-form "input[type=text]")]
    (when-let [task (trim (dommy/value input))]
      {:id nil :description task})))

(defn new-unplanned-task [ev]
  (.preventDefault ev)
  (let [form (.-target ev)]
    (let [root (dommy/closest form :#followup)]
      (when-let [task (read-new-task form)]
        (set-unplanned-task-form-state form true)
        (api/post "/unplanned"
                  :data [task]
                  :on-success (partial task-added root))
        (start-request root)))))

(defn create-root []
  (let [root (node [:#followup])]
    (dommy/listen! [root [:.task]] :change toggle-task-input)
    (dommy/listen! [root :#new-unplanned-task] :submit new-unplanned-task)
    (dommy/listen! [root :button.next] :click next-page)
    root))

(defn ^:export show []
  (let [root (create-root)]
    (api/get "/latest" :on-success (partial followup-fetched root))
    root))
