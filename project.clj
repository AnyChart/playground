(defproject playground "0.1.0-SNAPSHOT"
  :description "AnyChart Playground"
  :url "http://example."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :uberjar-name "pg-standalone.jar"
  :source-paths ["src" "src-cljc"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]

                 [org.immutant/web "2.1.6"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 [com.stuartsierra/component "0.3.2"]
                 [ring-middleware-format "0.7.2"]
                 [toml "0.1.2"]
                 [cheshire "5.7.0"]
                 [compojure "1.5.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-http "3.5.0"]
                 [version-clj "0.1.2"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [org.apache.commons/commons-lang3 "3.5"]
                 [com.anychart/playground-samples-parser "0.1.3"]
                 [enlive "1.1.6"]
                 [selmer "1.10.7"]
                 [clj-time "0.13.0"]
                 [me.raynes/fs "1.4.6"]
                 [camel-snake-kebab "0.4.0"]

                 ;; db
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [yesql "0.5.3"]
                 ;[org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [mysql/mysql-connector-java "6.0.6"]
                 [com.taoensso/carmine "2.15.1"]

                 ;; git
                 ;[im.chit/gita "0.2.5"]
                 [org.eclipse.jgit/org.eclipse.jgit "4.5.0.201609210915-r"]
                 ;[clj-jgit "0.8.9"]
                 ;[ilevd/clj-jgit "0.8.8"]

                 ;; front-end
                 ;[org.clojure/clojurescript "1.9.495"]
                 [org.clojure/clojurescript "1.9.473"]
                 [reagent "0.6.1"]
                 [re-frame "0.9.2"]
                 ; [cljs-http "0.1.42"]
                 [cljs-ajax "0.5.9"]
                 ;[secretary "1.2.3"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [venantius/accountant "0.1.9"]]
  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5"]
            [lein-kibit "0.1.3"]]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src-cljs" "src-cljc"]
                        :compiler     {:output-to     "resources/public/js/playground.js"
                                       :optimizations :whitespace
                                       :pretty-print  true}}
                       {:id           "prod"
                        :source-paths ["src-cljs" "src-cljc"]
                        :compiler     {:output-to       "resources/public/js/playground.js"
                                       :optimizations   :advanced
                                       :pretty-print    false
                                       :pseudo-names    false
                                       :externs         ["codemirror_externs.js"]
                                       :closure-defines {"goog.DEBUG" false}}}]})
