(ns dupe.router.test
  (:require-macros [cemerick.cljs.test :refer (is deftest with-test run-tests testing)])
  (:require [dupe.router :refer [compile-route compile-route-fragment]]))

(deftest compile-route-fragement-test
  (is (= "foo" (compile-route-fragment "foo")))
  (is (= "([^/]+)" (compile-route-fragment :foo))))

(deftest compile-route-test
  (is (= "foo/([^/]+)/baz" (compile-route ["foo" :bar "baz"]))))
