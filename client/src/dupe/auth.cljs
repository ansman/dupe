(ns dupe.auth)

(def access-token-key "dupe:access-token")

(defn ^:export access-token []
  (.getItem js/localStorage access-token-key))

(defn ^:export set-access-token! [new-token]
  (.setItem js/localStorage access-token-key new-token))

(defn ^:export refresh-access-token! []
  (when-let [token (access-token)] (set-access-token! token)))

(defn ^:export authenticated? []
  (not (empty? (access-token))))
