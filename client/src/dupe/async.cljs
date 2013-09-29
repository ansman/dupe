(ns dupe.async)

(defrecord AsyncError [data])

(defn throw-if-error [err]
  (if (= (type err) AsyncError)
    (throw (:data err))
    err))
