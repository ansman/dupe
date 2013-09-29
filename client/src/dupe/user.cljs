(ns dupe.user
  (:require [dupe.api :as api]))

(def user (atom nil))

(defn set-user! [new-user]
  (reset! user new-user))

(defn user-fetched! [f new-user]
  (set-user! new-user)
  (f))

(defn fetch! [f]
  (api/get "/user" :on-success (partial user-fetched! f)))
