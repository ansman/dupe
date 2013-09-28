(ns routes
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST PUT DELETE ANY OPTIONS context]])
  (:require [compojure.route :as route]
            [clojure.data.json :as json]
            [model]))

(defn -extract-body [req]
  (-> req :body clojure.java.io/reader json/read))

(defn get-latest [req]
  (json/write-str (model/get-latest-report)))

(defn post-planned [req]
  (model/new-report (-extract-body req))
  "ok")

(defn post-unplanned [req]
  (model/update-report (-extract-body req))
  "ok")

(defn put-task [id req]
  (model/update-report id (get (-extract-body req) "done")))

(defn post-task-comments [id req]
  (model/add-task-comment id (get (-extract-body req) "comment")))

(defn show-options [req]
  {:status 200
   :headers {"Access-Control-Allow-Methods" "POST, GET, PUT, OPTIONS, DELETE"
             "Access-Control-Allow-Origin" "*"}})


(defn wrap-response [app]
  (fn [request]
    (update-in (merge {:status 200} (app request))
               [:headers] merge {"Content-Type" "application/json; charset=utf-8"
                                 "Access-Control-Allow-Origin" "*"})))


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
  (site (wrap-response #'all-routes)))
