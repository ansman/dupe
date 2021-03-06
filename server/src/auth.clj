(ns auth
  (:require [org.httpkit.client :as http]
            [ring.util.codec :as codec]
            [clojure.data.json :as json]
            [clojure.string]
            [clojure.data.codec.base64 :as b64]
            [datasource.users]))

(def client-id "719f4d75d0d8adb52095")
(def client-secret "f3ee5554f8e829f888ed599ae5d998acfbd9b4d9")
(def oauth-url "https://github.com/login/oauth/authorize")
(def github-fields ["id" "login" "name" "email" "avatar_url"])


(defn encode-b64 [string]
  (-> string .getBytes b64/encode String.))

(defn decode-b64 [string]
  (-> string .getBytes b64/decode String.))

(defn uuid []
  (clojure.string/replace (str (java.util.UUID/randomUUID)) "-" ""))


(defn -create-callback-url [redirect-url]
  (codec/url-encode
    (str "http://dupe.clojurecup.com/api/auth/callback/" (encode-b64 redirect-url))))


(defn auth-request-url [redirect-url]
  (format "%s?redirect_uri=%s&client_id=%s"
          oauth-url
          (-create-callback-url redirect-url)
          client-id))


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


(defn -fetch-and-store-user [system github-access-token]
  (let [github-user (-fetch-github-user github-access-token)
        access-token (uuid)]
    (datasource.users/insert-or-update-user (:db system)
      (assoc (select-keys github-user github-fields)
             "github_access_token" github-access-token
             "access_token" access-token))
    access-token))

(defn callback [system code]
  (->> code -exchange-token (-fetch-and-store-user system)))

(defn is-valid-access-token [system access-token]
  (let [res
    (and
      access-token
      (datasource.users/get-user-by-access-token (:db system) access-token))]
    res))
