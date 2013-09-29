(ns dupe.auth
  (:require [goog.net.cookies]))

(def access-token-key "dupe:access-token")

(defn ^:export access-token
  ([] (.get goog.net.cookies access-token-key))
  ([new-token] (.set goog.net.cookies access-token-key
                     new-token
                     (* 60 60 24 7) ; One week validity
                     "/")))

(defn ^:export refresh-access-token []
  (when-let [token (access-token)] (access-token token)))

(defn ^:export authenticated? []
  (true? (access-token)))
