(ns dupe.views.auth
  (:require [dupe.auth :as auth]
            [dupe.router :as router]
            [dupe.api :as api]
            [dupe.config :refer [config]]
            [dommy.core :as dommy])
  (:require-macros [dommy.macros :refer [deftemplate node sel1 sel]]))

(def auth-callback (str (:url-root config) "#auth"))

(def auth-url (api/build-url
                (str "/auth/redirect?redirect_url="
                     (js/encodeURIComponent auth-callback))))

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

(defn handle-auth-token [token]
  (auth/set-access-token! token)
  (router/navigate ""))
