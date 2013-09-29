(ns routes
  (:use [ring.util.response :only [redirect]]
        [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST PUT DELETE ANY OPTIONS context]])
  (:require [compojure.route :as route]
            [clojure.data.json :as json]
            [ring.middleware.logger :as logger]
            [model]
            [auth]))

(def auth-redirect-url "/api/auth/redirect")

(defn -extract-body [req]
  (-> req :body clojure.java.io/reader json/read))

(defn get-latest [req]
  (json/write-str (model/get-latest-report)))

(defn post-planned [req]
  (model/new-report (-extract-body req))
  (json/write-str "ok"))

(defn post-unplanned [req]
  (-> req
    -extract-body
    model/update-report
    json/write-str))

(defn put-task [id req]
  (model/update-task id (get (-extract-body req) "done")))

(defn post-task-comments [id req]
  (model/add-task-comment id (get (-extract-body req) "comment")))

(defn redirect-to-auth [req]
  (redirect auth/auth-request-url))

(defn handle-auth-callback [req]
  (let [token (-> req :query-params (get "code") auth/callback)]
    (redirect (str "/?access_token=" token))))


(defn show-options [req]
  {:status 200
   :headers {"Access-Control-Allow-Methods" "POST, GET, PUT, OPTIONS, DELETE"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Allow-Headers" "Content-Type"}})


(defn wrap-response [app]
  (fn [request]
    (update-in (merge {:status 200} (app request))
               [:headers] merge {"Content-Type" "application/json; charset=utf-8"
                                 "Access-Control-Allow-Origin" "*"
                                 "Access-Control-Allow-Headers" "Content-Type"})))

(defn require-auth [app]
  (fn [request]
    (if (-> request
          :query-params
          (get "access_token")
          auth/is-valid-access-token
          nil?)
      {:status 200 :body (json/write-str {"redirect_url" auth-redirect-url})}
      (app request))
  ))

(defroutes all-routes
  (OPTIONS "*" [] show-options)
  (GET "/api/latest" [] get-latest)
  (POST "/api/planned" [] post-planned)
  (POST "/api/unplanned" [] post-unplanned)
  (PUT "/api/tasks/:id" [id] #(put-task (Integer. id) %))
  (POST "/api/tasks/:id/comments" [id] #(post-task-comments (Integer. id) %))
  (GET "/api/auth/redirect" [] redirect-to-auth)
  (GET "/api/auth/callback" [] handle-auth-callback)
  (route/files "/static/")
  (route/not-found "<p>Page not found.</p>"))

(defn app []
  (-> #'all-routes
    wrap-response
    require-auth
    logger/wrap-with-plaintext-logger
    site))
