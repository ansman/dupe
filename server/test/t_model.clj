(ns t-model
  (:use midje.sweet)
  (:require [model]))

(fact "true is never false"
  true => true)
