(ns dupe.router
  (:require [clojure.string :refer [join]]))

(def routes (atom {}))

(defn navigate [url]
  (set! (.-hash js/window.location) (str "#" url)))

(defn compile-route-fragment [fragment]
  (if (keyword? fragment) "([^/]+)" fragment))

(defn compile-route [fragments]
  "Takes a seq of strings/keywords and compiles it to a regexp"
  (re-pattern (str "^#?" (join "/" (map compile-route-fragment fragments)) "$")))

(defn call-route [matcher callback]
  (let [result (.exec matcher js/location.hash)]
    (when result (apply callback (rest result)))))

(defn defroute [url-parts callback]
  (swap! routes conj {(compile-route url-parts) callback}))

(defn try-route [entry]
  (when-let [result (.exec (key entry) (.-hash js/location))]
    (apply (val entry) (rest result))
    true))

(defn try-routes []
  (when (empty? (filter try-route @routes))
    (.warn js/console "404")))

(.addEventListener js/window "hashchange" (fn [ev] (try-routes)))

(def start try-routes)
