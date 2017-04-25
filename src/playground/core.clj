(ns playground.core
  (:require [toml.core :as toml]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [clojure.spec :as s]
            [playground.spec.app-config :as core-spec]
            [playground.db.core :as db]
            [playground.web.core :as web]
            [playground.generator.core :as generator]
            [playground.notification.slack :as slack]
            [playground.redis.core :as redis]
            [playground.preview-generator.core :as pw-generator])
  (:gen-class)
  (:import (org.slf4j LoggerFactory Logger)
           (ch.qos.logback.classic Level)))

; disable some dirty logging
(System/setProperties
  (doto (java.util.Properties. (System/getProperties))
    (.put "com.mchange.v2.log.MLog" "com.mchange.v2.log.FallbackMLog")
    (.put "com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL" "OFF")))

(.setLevel (LoggerFactory/getLogger Logger/ROOT_LOGGER_NAME) Level/INFO)

(defn repositories-conf [conf]
  (map
    #(atom (update % :type keyword))
    (:repositories conf)))

(defn get-full-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :notifier (slack/new-notifier (-> conf :notifications :slack))
    :generator (component/using (generator/new-generator {:templates (:templates conf)} (repositories-conf conf))
                                [:db :notifier :redis])
    :preview-generator (component/using (pw-generator/new-preview-generator (:previews conf))
                                        [:db :redis :notifier])
    :web (component/using (web/new-web (merge (:web conf) (:previews conf)))
                          [:db :redis])))

(defn get-worker-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :notifier (slack/new-notifier (-> conf :notifications :slack))
    :generator (component/using (generator/new-generator {:templates (:templates conf)} (repositories-conf conf))
                                [:db :notifier :redis])))

(defn get-web-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :web (component/using (web/new-web (:web conf)) [:db :redis])))

(defn get-preview-worker-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :notifier (slack/new-notifier (-> conf :notifications :slack))
    :preview-generator (component/using (pw-generator/new-preview-generator (merge (:web conf) (:previews conf)))
                                        [:db :redis :notifier])))

(def system nil)

(defn read-config [path]
  (toml/read (slurp path) :keywordize))

(defn -main [conf-path & args]
  (let [conf (read-config conf-path)]
    (if (= (s/conform ::core-spec/config conf) ::s/invalid)
      (timbre/info "Bad config file!\n" (s/explain-str ::core-spec/config conf))
      (let [sys (case (:mode conf)
                  "web" (get-web-system conf)
                  "generator" (get-worker-system conf)
                  "preview-generator" (get-preview-worker-system conf)
                  (get-full-system conf))]
        (alter-var-root #'system (constantly (component/start-system sys)))
        system))))

(defn stop []
  (when system
    (alter-var-root #'system component/stop-system)))
