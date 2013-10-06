(ns t-smoke
  (:use midje.sweet)
  (:require [ring.mock.request :as req]
            [clojure.data.json :as json]
            [routes]
            [model]
            [datasource.users]))


(def handler (routes/app))

(defn insert-user [user]
  (datasource.users/insert-or-update-user (:db @routes/system)
    (merge {"id" 17 "access_token" "abcde"} user)))


(defn do-request [method uri]
  (handler (req/request :method uri)))

(defn authed-get [uri]
  (-> (req/request :get uri)
        (req/query-string {"access_token" "abcde"})
        handler))

(defn authed-post [uri json-body]
  (-> (req/request :post uri)
        (req/query-string {"access_token" "abcde"})
        (req/header "Content-Type" "application/json")
        (req/body (json/write-str json-body))
        handler))

(defn load-body [resp]
  (-> resp :body (json/read-str :key-fn keyword)))


(defn setup []
  (model/init @routes/system)
  (insert-user {}))

(with-state-changes
  [(before :facts (setup))]

  (fact "Most endpoints are forbidden if you do not have a valid access token"
    (:status (do-request :get "/")) => 401
    (:status (do-request :get "/api/latest")) => 401
    (:status (do-request :get "/api/user")) => 401
  )

  (fact "However, with a valid access token you'll be fine"
    (:status (authed-get "/")) => 404
    (:status (authed-get "/api/latest")) => 200
    (:status (authed-get "/api/user")) => 200
  )

  (fact "Planning your day without submitting any tasks"
    (-> (authed-post "/api/planned" []) :status) => 200
    (-> (authed-get "/api/latest") :status) => 200
    (-> (authed-get "/api/latest") load-body) => {:planned []
                                                      :unplanned []}
  )

  (fact "Planning your day and submitting one task"
    (-> (authed-post "/api/planned" [{:description "My task"}]) :status) => 200
    (let [resp (authed-get "/api/latest")]
      (-> resp :status) => 200
      (-> resp load-body :planned first :description) => "My task"))
  )


(with-state-changes
  [(before :facts (do (setup) (authed-post "/api/planned" [])))]
  (fact "When you already have a plan"

    (fact "Planning a new day changes your current plan"
      (-> (authed-post "/api/planned" [{:description "My other task"}]) :status) => 200
      (let [resp (authed-get "/api/latest")]
        (-> resp :status) => 200
        (-> resp load-body :planned count) => 1
        (-> resp load-body :planned first :description) => "My other task"))

    (fact "You can add unplanned tasks"
      (-> (authed-post "/api/unplanned" [{:description "My unplanned task"}]) :status) => 200
      (let [resp (authed-get "/api/latest")]
        (-> resp :status) => 200
        (-> resp load-body :unplanned count) => 1
        (-> resp load-body :unplanned first :description) => "My unplanned task"))

  ))
