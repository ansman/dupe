(ns dupe.api
  (:require [dupe.config :refer [config]]
            [cljs.core.async :refer [chan close! >!]]
            [goog.json :as json]
            [clojure.walk :refer [keywordize-keys]]
            [shoreleave.remotes.request :as request])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle-success [clbk data]
  (when clbk
    (clbk (keywordize-keys (js->clj (json/parse (:body data)))))))

(defn handle-error [clbk & {:keys [status body]}]
  (if clbk
    (clbk status body)
    (js/alert (str "The server failed with status " (or status "unknown")))))

(defn build-url [endpoint] (str (:api-url config) endpoint))

(defn ^:export get [endpoint & {:keys [on-error on-success]}]
  (request/request (build-url endpoint)
                   :on-success (partial handle-success on-success)
                   :on-error (partial handle-error on-error)))

(defn ^:export post [endpoint & {:keys [data on-error on-success]}]
  (request/request [:post (build-url endpoint)]
                   :headers {:content-type "application/json"}
                   :content (json/serialize (clj->js data))
                   :on-success (partial handle-success on-success)
                   :on-error (partial handle-error on-error)))

(defn ^:export put [endpoint & {:keys [data on-error on-success]}]
  (request/request [:put (build-url endpoint)]
                   :headers {:content-type "application/json"}
                   :content (json/serialize (clj->js data))
                   :on-success (partial handle-success on-success)
                   :on-error (partial handle-error on-error)))