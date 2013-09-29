(ns dupe.macros.async)

(defmacro <? [ch]
  `(dupe/async.throw-if-error (~'<! ~ch)))

(defmacro throw>! [ch data]
  `(~'>! ~ch (dupe/async.AsyncError. ~data)))
