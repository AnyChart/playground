(defproject playground "0.1.0-SNAPSHOT"
  :description "AnyChart Playground"
  :url "http://example."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :uberjar-name "pg-standalone.jar"
  :source-paths ["src" "src-cljc"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]

                 [org.immutant/web "2.1.9"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 [com.stuartsierra/component "0.3.2"]
                 [ring-middleware-format "0.7.2"]
                 ;[ring "1.6.0"]
                 [toml "0.1.2"]
                 [cheshire "5.8.0"]
                 [compojure "1.6.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-http "3.7.0"]
                 [version-clj "0.1.2"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [org.apache.commons/commons-lang3 "3.7"]
                 [enlive "1.1.6"]
                 [org.jsoup/jsoup "1.11.2"]
                 [selmer "1.11.3"]
                 [clj-time "0.14.2"]
                 [me.raynes/fs "1.4.6"]
                 [camel-snake-kebab "0.4.0"]
                 [hiccup "1.0.5"]

                 ;; db
                 [mpg "1.3.0"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
                 [yesql "0.5.3"]
                 ;[org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/java.jdbc "0.7.4"]
                 [mysql/mysql-connector-java "6.0.6"]
                 [com.taoensso/carmine "2.16.0"]

                 ;; crypto
                 [buddy "2.0.0"]
                 [crypto-password "0.2.0"]

                 ;; git
                 ;[im.chit/gita "0.2.5"]
                 [org.eclipse.jgit/org.eclipse.jgit "4.5.0.201609210915-r"]
                 ;[clj-jgit "0.8.9"]
                 ;[ilevd/clj-jgit "0.8.8"]

                 ;; phantom
                 [org.imgscalr/imgscalr-lib "4.2"]

                 ;; front-end
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.2"]
                 [re-com "2.1.0"]
                 [alandipert/storage-atom "2.0.1"]
                 ; [cljs-http "0.1.42"]
                 [cljs-ajax "0.7.3"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [hiccups "0.3.0"]
                 ;[secretary "1.2.3"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [venantius/accountant "0.2.3"]]
  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5"]
            [lein-kibit "0.1.3"]
            [deraen/sass4clj "0.3.1"]
            [deraen/lein-sass4clj "0.3.1"]]
  :sass {:source-paths ["src-css/scss"]
         :target-path  "resources/public/css"
         :output-style :compressed}
  :profiles {:dev {:jvm-opts     ["-Dlocal=true"]
                   :dependencies [[re-frisk "0.5.3"]]}}
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
