(ns routes
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST PUT DELETE ANY OPTIONS context]])
  (:require [compojure.route :as route]
            [clojure.data.json :as json]
            [ring.middleware.logger :as logger]
            [model]))

(defn -extract-body [req]
  (-> req :body clojure.java.io/reader json/read))

(defn get-latest [req]
  (json/write-str (model/get-latest-report)))

(defn post-planned [req]
  (model/new-report (-extract-body req))
  (json/write-str "ok"))

(defn post-unplanned [req]
  (model/update-report (-extract-body req))
  (json/write-str "ok"))

(defn put-task [id req]
  (model/update-report id (get (-extract-body req) "done")))

(defn post-task-comments [id req]
  (model/add-task-comment id (get (-extract-body req) "comment")))

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


(defroutes all-routes
  (OPTIONS "*" [] show-options)
  (GET "/api/latest" [] get-latest)
  (POST "/api/planned" [] post-planned)
  (POST "/api/unplanned" [] post-unplanned)
  (PUT "/api/tasks/:id/" [id] #(put-task (Integer. id) %))
  (POST "/api/tasks/:id/comments" [id] #(post-task-comments (Integer. id) %))
  (route/files "/static/")
  (route/not-found "<p>Page not found.</p>"))

(defn app []
  (-> #'all-routes
    wrap-response
    logger/wrap-with-plaintext-logger
    site))
