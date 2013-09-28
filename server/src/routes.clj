(ns routes
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]])
  (:require [compojure.route :as route])
  )

(defn show-landing-page [req]
  "weee!")

(defroutes all-routes
  (GET "/" [] show-landing-page)
  (route/files "/static/")
  (route/not-found "<p>Page not found.</p>"))

(defn app []
  (site #'all-routes))
