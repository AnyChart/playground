(defproject playground "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :uberjar-name "pg-standalone.jar"
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [org.immutant/web "2.1.6"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 [toml "0.1.1"]
                 [cheshire "5.7.0"]
                 [compojure "1.5.2"]
                 [com.taoensso/timbre "4.8.0"]

                 [com.stuartsierra/component "0.3.2"]

                 [com.anychart/playground-samples-parser "0.1.2"]
                 [enlive "1.1.6"]

                 ;; db
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [yesql "0.5.3"]
                 ;[org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [mysql/mysql-connector-java "6.0.5"]


                 [im.chit/gita "0.2.5"]
                 ;[org.eclipse.jgit/org.eclipse.jgit "4.5.0.201609210915-r"]
                 ;[clj-jgit "0.8.9"]
                 ;[ilevd/clj-jgit "0.8.8"]
                 [me.raynes/fs "1.4.6"]]
  :plugins [[lein-ancient "0.6.10"]])
