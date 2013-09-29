(ns auth
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [db]))

(def client-id "719f4d75d0d8adb52095")
(def client-secret "f3ee5554f8e829f888ed599ae5d998acfbd9b4d9")
(def oauth-url "https://github.com/login/oauth/authorize")
(def auth-request-url (str oauth-url "?client_id=" client-id))

(defn -exchange-token [code]
  (let [resp @(http/post "https://github.com/login/oauth/access_token"
              {:headers {"Accept" "application/json"}
               :form-params
                {:client_id client-id
                 :client_secret client-secret
                 :code code}})]
    (println "Exchange token; github says:" resp)
    (-> resp :body json/read-str (get "access_token"))))

(defn -fetch-github-user [github-access-token]
  (let [resp @(http/get
                (str "https://api.github.com/user?access_token="
                     github-access-token)
                {:headers {"Accept" "application/json"}})]
    (println "Fetch user; github says:" resp)
    (-> resp :body json/read-str)))


(def github-fields ["id" "login" "name" "email" "avatar_url"])

(defn callback [code]
  (let [token (-exchange-token code)
        github-user (-fetch-github-user token)]
    (db/insert-or-update-user
      (assoc (select-keys github-user github-fields) "access_token" token))
    token))

(defn is-valid-access-token [access-token]
  (let [res
    (and
      access-token
      (db/get-user-by-access-token access-token))]
    res))
