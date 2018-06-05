(defproject playground "1.0.0-SNAPSHOT"
  :description "AnyChart Playground is an online tool for testing and showcasing user-created HTML, CSS and JavaScript code snippets."
  :url "https://playground.anychart.com/"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :uberjar-name "pg-standalone.jar"
  :source-paths ["src" "src-cljc"]
  :java-source-paths ["src-java"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]

                 [org.immutant/web "2.1.10"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 [com.stuartsierra/component "0.3.2"]
                 [ring-middleware-format "0.7.2"]
                 ;[ring "1.6.0"]
                 [toml "0.1.3"]
                 [cheshire "5.8.0"]
                 [compojure "1.6.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-http "3.9.0"]
                 [version-clj "0.1.2"]
                 [com.cognitect/transit-clj "0.8.309"]
                 [org.apache.commons/commons-lang3 "3.7"]
                 [enlive "1.1.6"]
                 [org.jsoup/jsoup "1.11.3"]
                 [selmer "1.11.7"]
                 [clj-time "0.14.4"]
                 [me.raynes/fs "1.4.6"]
                 [camel-snake-kebab "0.4.0"]
                 [hiccup "1.0.5"]
                 [net.sf.jtidy/jtidy "r938"]
                 [instaparse "1.4.9"]
                 [com.rpl/specter "1.1.1"]

                 ;; db
                 [mpg "1.3.0"]
                 ;[org.postgresql/postgresql "9.4.1207"]
                 [org.postgresql/postgresql "42.2.2"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
                 [yesql "0.5.3"]
                 ;[org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/java.jdbc "0.7.6"]
                 [mysql/mysql-connector-java "6.0.6"]
                 [com.taoensso/carmine "2.18.1"]
                 [cc.qbits/spandex "0.6.2"]

                 ;; crypto
                 [buddy "2.0.0"]
                 [crypto-password "0.2.0"]

                 ;; git
                 ;[im.chit/gita "0.2.5"]
                 [org.eclipse.jgit/org.eclipse.jgit "4.11.0.201803080745-r"]
                 ;[clj-jgit "0.8.9"]
                 ;[ilevd/clj-jgit "0.8.8"]

                 ;; phantom
                 [com.google.guava/guava "22.0"]
                 [com.github.detro.ghostdriver/phantomjsdriver "1.1.0"]
                 [clj-webdriver "0.7.2"]
                 [org.imgscalr/imgscalr-lib "4.2"]
                 [com.climate/claypoole "1.1.4"]

                 ;; front-end
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.8.0"]
                 [re-frame "0.10.5"]
                 [re-com "2.1.0"]
                 [alandipert/storage-atom "2.0.1"]
                 ; [cljs-http "0.1.42"]
                 [cljs-ajax "0.7.3"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [hiccups "0.3.0"]
                 [secretary "1.2.3"]
                 [com.cognitect/transit-cljs "0.8.256"]
                 [venantius/accountant "0.2.4"]

                 ;; maxcdn dependencies
                 ;; https://mvnrepository.com/artifact/commons-codec/commons-codec
                 [commons-codec/commons-codec "1.11"]
                 ;; https://mvnrepository.com/artifact/org.scribe/scribe
                 [org.scribe/scribe "1.3.7"]
                 ;; https://mvnrepository.com/artifact/org.json/json
                 [org.json/json "20180130"]]
  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5"]
            [lein-kibit "0.1.3"]
            [deraen/sass4clj "0.3.1"]
            [deraen/lein-sass4clj "0.3.1"]
            [lein-asset-minifier "0.4.4"]]
  :sass {:source-paths ["src-css/scss"]
         :target-path  "resources/public/css"
         :output-style :compressed}
  :minify-assets [
                  ;[:html {:source "dev/resource/html" :target "dev/minified/html"}]
                  ;[:css {:source "dev/resources/css" :target "dev/minified/css/styles.min.css"}]
                  [:js {:source ["resources/public/codemirror/lib/codemirror.js"
                                 "resources/public/codemirror/addon/scroll/simplescrollbars.js"
                                 "resources/public/codemirror/mode/javascript/javascript.js"
                                 "resources/public/codemirror/mode/css/css.js"
                                 "resources/public/codemirror/mode/xml/xml.js"
                                 "resources/public/codemirror/mode/htmlmixed/htmlmixed.js"]
                        :target "resources/public/js/codemirror.min.js"}]
                  [:js {:source ["resources/public/js/clipboard.min.js"
                                 "resources/public/splitter/splitter.js"
                                 "resources/public/js/Sortable.min.js"]
                        :target "resources/public/js/clipboard-splitter-sortable.min.js"}]]
  :profiles {:dev {:jvm-opts     ["-Dlocal=true"]
                   :dependencies [[re-frisk "0.5.4"]]}}
  :cljsbuild {:builds [
                       ;; editor
                       {:id           "dev"
                        :source-paths ["src-cljs" "src-cljc"]
                        :compiler     {:output-to     "resources/public/js/playground.js"
                                       :optimizations :whitespace
                                       :pretty-print  true
                                       :preloads      [re-frisk.preload]}}
                       {:id           "prod"
                        :source-paths ["src-cljs" "src-cljc"]
                        :compiler     {
                                       ;:output-dir "resources/public/js"
                                       :output-to       "resources/public/js/playground.js"
                                       :optimizations   :advanced
                                       :pretty-print    false
                                       :pseudo-names    false
                                       :externs         ["codemirror_externs.js"]
                                       :closure-defines {"goog.DEBUG" false}
                                       ;:source-map "resources/public/js/playground.js.map"
                                       }}
                       ;; site
                       {:id           "dev-site"
                        :source-paths ["src-site-cljs" "src-cljc"]
                        :compiler     {:output-to     "resources/public/js/site.js"
                                       :optimizations :whitespace
                                       :pretty-print  true}}
                       {:id           "prod-site"
                        :source-paths ["src-site-cljs" "src-cljc"]
                        :compiler     {:output-to       "resources/public/js/site.js"
                                       :optimizations   :advanced
                                       :pretty-print    false
                                       :pseudo-names    false
                                       :closure-defines {"goog.DEBUG" false}}}]})
