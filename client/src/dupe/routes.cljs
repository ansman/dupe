(ns dupe.routes
  (:require [dupe.router :refer [defroute]]
            [dupe.layouts.application :as application]
            [dupe.views.followup :as followup]
            [dupe.views.plan :as plan]))

(defn layout-with-view [layout view]
  (fn [ & args]
    (layout (apply view args))))

(defn application-layout-with-view [view]
  (layout-with-view application/show view))

(defroute [] (application-layout-with-view followup/show))
(defroute ["plan"] (application-layout-with-view plan/show))
