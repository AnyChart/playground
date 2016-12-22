(defproject playground "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:aot playground.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]

                 [org.immutant/web "2.1.5"]
                 ;[org.immutant/caching "2.1.5"]
                 ;[org.immutant/messaging "2.1.5"]
                 ;[org.immutant/scheduling "2.1.5"]
                 ;[org.immutant/transactions "2.1.5"]

                 ]
  :plugins [[lein-ancient "0.6.10"]])
