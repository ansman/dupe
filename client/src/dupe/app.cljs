(ns dupe.app
  (:require [dupe.api :as api]
            [dupe.router :as router]
            [dupe.routes]
            [dupe.auth :as auth]
            [dupe.user :as user]
            [dommy.core :as dommy]
            [clojure.browser.repl :as repl]
            [cljs.core.async :refer [chan >! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [dommy.macros :refer [node deftemplate sel1 sel]]
                   [dupe.macros.async :refer [throw>! <?]]))

(defn ^:export start []
  (if (auth/authenticated?)
    (user/fetch! router/start)
    (router/start)))
