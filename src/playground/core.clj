(ns playground.core
  (:require [toml.core :as toml]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]

            [playground.db.core :as db]
            [playground.web.core :as web]
            [playground.generator.core :as generator]

            [playground.repo.git :as git])
  (:gen-class))

; disable some dirty logging
(System/setProperties
  (doto (java.util.Properties. (System/getProperties))
    (.put "com.mchange.v2.log.MLog" "com.mchange.v2.log.FallbackMLog")
    (.put "com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL" "OFF")))

(defn repositories-conf [conf]
  (map
    #(atom (update % :type keyword))
    (:repositories conf)))

(defn get-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :generator (component/using (generator/new-generator {} (repositories-conf conf)) [:db])
    :web (component/using (web/new-web (:web conf)) [:db :generator])
    ))

(def system nil)

(defn -main [& args]
  (let [conf (toml/read (slurp (first args)) :keywordize)
        sys (get-system conf)]
    (timbre/info conf)
    (timbre/info sys)
    (alter-var-root #'system (constantly (component/start-system sys)))
    (generator/check-repositories (:generator system) (:db system))
    system))

(defn stop []
  (when system
    (alter-var-root #'system component/stop-system)))

(defn t []
  (-main "/media/ssd/sibental/playground-data/conf.toml"))

(defn t1 []
  (toml/read (slurp "/media/ssd/sibental/playground-data/conf.toml") :keywordize))