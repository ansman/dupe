(ns dupe.layouts.application
  (:require [dommy.core :as dommy]
            [dupe.user :refer [user]]
            [clojure.string :refer [split]])
  (:require-macros [dommy.macros :refer [deftemplate sel1]]))

(deftemplate render [el]
  [:nav.navbar.navbar-default.navbar-fixed-top
   [:.container
    [:.navbar-header
     (if @user
       [:button.navbar-toggle {:type "button"
                               :data-toggle "collapse"
                               :data-target ".navbar-collapse"}
        [:span.sr-only "Toggle navigation"]
        (map (fn [i] [:span.icon-bar]) (range 3))])
     [:a.navbar-brand {:href "#"} "Dupe"]]
    (if @user
      [:.collapse.navbar-collapse
       [:ul.nav.navbar-nav.navbar-right
        [:li.dropdown
         [:a.dropdown-toggle {:href "#" :data-toggle "dropdown"}
          (str "Logged in as "
               (first (split (:name @user) " ")))
          [:b.caret]]
         [:ul.dropdown-menu
          [:li
           [:a {:href "#logout"}
            [:span.glyphicon.glyphicon-log-out]
            " Log out"]]]]]])]]
  [:.container el])

(defn ^:export show [el]
  (dommy/replace-contents! (sel1 :#content) (render el)))
