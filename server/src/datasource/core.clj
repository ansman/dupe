(ns datasource.core
  (:require [datasource.users]
            [datasource.reports]
            [datasource.tasks]))


(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/dupe"
   :user "dupe"
   :password "dupe"})

(defn reset-all [db]
  (datasource.users/reset db)
  (datasource.reports/reset db)
  (datasource.tasks/reset db))
