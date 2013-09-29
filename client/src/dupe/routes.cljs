(ns dupe.routes
  (:require [dupe.router :refer [defroute]]
            [dupe.layouts.application :as application]
            [dupe.views.followup :as followup]
            [dupe.views.auth :as auth]
            [dupe.views.plan :as plan]))

(defn layout-with-view [layout view]
  (fn [ & more]
    (when-let [el (apply view more)]
      (layout el))))

(defn application-layout-with-view [view]
  (layout-with-view application/show view))

(defroute [] (application-layout-with-view auth/show))
(defroute ["logout"] (application-layout-with-view auth/log-out))
(defroute ["auth" :token] auth/handle-auth-token)
(defroute ["followup"] (application-layout-with-view followup/show))
(defroute ["plan"] (application-layout-with-view plan/show))
