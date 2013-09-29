(ns dupe.views.auth
  (:require [dupe.auth :as auth]
            [dupe.router :as router]
            [dupe.api :as api]
            [dommy.core :as dommy])
  (:require-macros [dommy.macros :refer [deftemplate node sel1 sel]]))

(def auth-url (api/build-url "/auth/redirect"))

(deftemplate render []
  [:a.github-connect.btn.btn-primary.btn-lg {:href auth-url}
   [:i.github-icon]
   "Connect with GitHub"])

(defn create-root []
  (let [root (node [:#auth])]
    root))

(defn show []
  (if (auth/authenticated?)
    (do
      (router/navigate "followup")
      nil)
    (let [root (create-root)]
      (dommy/replace-contents! root (render))
      root)))
