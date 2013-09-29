(ns dupe.app
  (:require [dupe.api :as api]
            [dupe.router :as router]
            [dupe.routes]
            [dommy.core :as dommy]
            [clojure.browser.repl :as repl]
            [cljs.core.async :refer [chan >! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [dommy.macros :refer [node deftemplate sel1 sel]]
                   [dupe.macros.async :refer [throw>! <?]]))

(defn ^:export start [] (router/start))

; (deftemplate render-task [{:keys [description]}]
;   [:div.task description])

; (deftemplate render-tasks [tasks]
;   [:div#tasks (map render-task tasks)])

; (defn add-tasks [{:keys [planned]}]
;   (dommy/append! (sel1 :body) (render-tasks planned)))

; (defn ^:export start [n]
;   (api/get "/latest" :on-success add-tasks))

; (def c (chan))

; (go (try
;       (let [value (<? c)]
;         (.log js/console (str "Got value " c)))
;       (catch js/Object e
;         (.log js/console "Got error:" e))))

; (go (throw>! c (js/Error. "Fail")))

; (repl/connect "http://localhost:9000/repl")
