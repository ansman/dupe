(ns routes
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY OPTIONS context]])
  (:require [compojure.route :as route])
  )

(defn show-landing-page [req]
  "weee!")


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
  (GET "/" [] show-landing-page)
  (OPTIONS "*" [] show-options)
  (route/files "/static/")
  (route/not-found "<p>Page not found.</p>"))

(defn app []
  (site (wrap-response #'all-routes)))
