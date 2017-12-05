(ns playground.db.core
  (:require [clojure.java.jdbc :as clj-jdbc :refer [quoted]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(defn- connection-pool
  "Create a connection pool for the given database spec."
  [{:keys [subprotocol subname classname user password
           excess-timeout idle-timeout minimum-pool-size maximum-pool-size
           test-connection-query
           idle-connection-test-period
           test-connection-on-checkin
           test-connection-on-checkout]
    :or   {excess-timeout              (* 30 60)
           idle-timeout                (* 3 60 60)
           minimum-pool-size           3
           maximum-pool-size           15
           test-connection-query       nil
           idle-connection-test-period 0
           test-connection-on-checkin  false
           test-connection-on-checkout false}}]
  {:datasource (doto (ComboPooledDataSource.)
                 (.setDriverClass classname)
                 (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
                 (.setUser user)
                 (.setPassword password)
                 (.setMaxIdleTimeExcessConnections excess-timeout)
                 (.setMaxIdleTime idle-timeout)
                 (.setMinPoolSize minimum-pool-size)
                 (.setMaxPoolSize maximum-pool-size)
                 (.setIdleConnectionTestPeriod idle-connection-test-period)
                 (.setTestConnectionOnCheckin test-connection-on-checkin)
                 (.setTestConnectionOnCheckout test-connection-on-checkout)
                 (.setPreferredTestQuery test-connection-query))})

(defn create-db-spec [conf]
  {:classname   "com.mysql.cj.jdbc.Driver"
   :subprotocol "mysql"
   :subname     (str "//" (:host conf) ":" (:port conf) "/" (:name conf) "?characterEncoding=UTF-8&serverTimezone=UTC&useUnicode=true&useSSL=false")
   :user        (:user conf)
   :password    (:password conf)
   :stringtype  "unspecified"})

(defn create-db-spec-postgre [conf]
  {:subprotocol "postgresql"
   :subname     (str "//" (:host conf) ":" (:port conf) "/" (:name conf))
   :classname   "org.postgresql.Driver"
   :user        (:user conf)
   :password    (:password conf)})

(defrecord JDBC [config db-spec conn]
  component/Lifecycle

  (start [this]
    (timbre/info "DB start")
    ;(prn this)
    (if conn
      (assoc this :conn conn)
      (let [db-spec (create-db-spec-postgre config)
            conn (connection-pool db-spec)]
        (assoc this :db-spec db-spec :conn conn))))

  (stop [this]
    (timbre/info "DB stop")
    (when (:conn this)
      (-> this :conn :datasource (.close))
      this)))

(defn new-jdbc [config]
  (map->JDBC {:config config}))

;(defn sql [q]
;  (println (sql/format q :quoting :ansi))
;  (sql/format q :quoting :ansi))
;
;(defn query [jdbc q]
;  (clj-jdbc/query (:conn jdbc) (sql q)))
;
;(defn one [jdbc q]
;  (first (query jdbc q)))
;
;(defn exec [jdbc q]
;  (clj-jdbc/execute! (:conn jdbc) (sql q)))
;
;(defn insert! [jdbc table data]
;  (clj-jdbc/insert! (:conn jdbc) table data))
;
(defn insert-multiple! [db table data]
  (if (seq data)
    (clj-jdbc/insert-multi! (:conn db) table data
                            ;{:entities (quoted \`)}
                            )))
