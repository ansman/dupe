(ns datasource.users
  (:require [clojure.java.jdbc :as j]
            [datasource.util]))

(defn reset [db]
  (datasource.util/truncate-table db :users))

(def insert-q
  "insert into users
    (id, github_access_token, access_token, login, name, email, avatar_url)
  values
    (?, ?, ?, ?, ?, ?, ?)
  on duplicate key update
    github_access_token = values(github_access_token),
    access_token = values(access_token),
    login = values(login),
    name = values(name),
    email = values(email),
    avatar_url = values(avatar_url)")

(defn insert-or-update-user [db user]
  (j/execute! db [insert-q
                       (get user "id")
                       (get user "github_access_token")
                       (get user "access_token")
                       (get user "login")
                       (get user "name")
                       (get user "email")
                       (get user "avatar_url")]))

(defn get-user-by-access-token [db access-token]
  (first
    (j/query db ["select * from users where access_token = ?" access-token])))
