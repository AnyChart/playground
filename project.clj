(defproject playground "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :uberjar-name "pg-standalone.jar"
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [org.immutant/web "2.1.5"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 [clj-toml "0.3.1" :exclusions [clj-time org.clojure/clojure]]
                 [compojure "1.5.1"]
                 [com.taoensso/timbre "4.8.0"]

                 ;; db
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [yesql "0.5.3"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [mysql/mysql-connector-java "6.0.5"]

                 ]
  :plugins [[lein-ancient "0.6.10"]])
