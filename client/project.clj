(defproject dupe "0.0.1"
  :plugins [[lein-cljsbuild "0.3.3"]]
  :hooks [leiningen.cljsbuild]
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1909"]
                 [org.clojure/core.async "0.1.222.0-83d0c2-alpha"]
                 [shoreleave "0.3.0"]
                 [prismatic/dommy "0.1.1"]
                 [com.cemerick/clojurescript.test "0.0.4"]]

  :cljsbuild {
    :builds {
      :development {
        :source-paths ["src" "development"]; "test"]
        :notify-command ["growlnotify" "-m"]
        :compiler {
          :pretty-print true
          :output-to "public/javascript/app.js"}}
      :production {
        :source-paths ["src" "production"]; "test"]
        :notify-command ["growlnotify" "-m"]
        :compiler {
          :pretty-print false
          :optimizations :simple
          :output-to "build/javascript/app.js"}}}
    :test-commands {"tests" ["phantomjs" :cljs.testrunner "public/javascript/app.js"]}})
