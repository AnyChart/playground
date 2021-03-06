(ns playground.core
  (:require [playground.spec.app-config :as core-spec]
            [playground.repo.git :as git]
            [playground.db.core :as db]
            [playground.elastic.core :as elastic]
            [playground.web.core :as web]
            [playground.generator.core :as generator]
            [playground.notification.core :as notifier]
            [playground.redis.core :as redis]
            [playground.preview-generator.core :as pw-generator]
            [playground.utils.utils :as utils]
            [playground.data.config :as c]
            [toml.core :as toml]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [clojure.spec.alpha :as s])
  (:gen-class)
  (:import (org.slf4j LoggerFactory Logger)
           (ch.qos.logback.classic Level)
           (java.nio.charset Charset)))


(defn git-commit []
  (try
    (git/current-commit (.getAbsolutePath (clojure.java.io/file "")))
    (catch Exception _ (quot (System/currentTimeMillis) 1000))))


(defmacro parse-data-compile-time []
  `'~(git-commit))


(def commit (parse-data-compile-time))



; disable some dirty logging
(System/setProperties
  (doto (java.util.Properties. (System/getProperties))
    (.put "com.mchange.v2.log.MLog" "com.mchange.v2.log.FallbackMLog")
    (.put "com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL" "OFF")))


(.setLevel (LoggerFactory/getLogger Logger/ROOT_LOGGER_NAME) Level/INFO)


(defn set-default-charset []
  (System/setProperty "file.encoding" "UTF-8")
  (doto (.getDeclaredField Charset "defaultCharset") (.setAccessible true) (.set nil nil))
  (timbre/info "Charset: " (System/getProperty "file.encoding")))


(defn get-full-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :elastic (component/using (elastic/new-elastic (-> conf :db :elastic))
                              [:db])
    :redis (redis/new-redis (:redis conf))
    :notifier (notifier/new-notifier (-> conf :notifications))
    :generator (component/using (generator/new-generator conf)
                                [:db :notifier :redis :elastic])
    :preview-generator (component/using (pw-generator/new-preview-generator (:previews conf))
                                        [:db :redis :notifier])
    :web (component/using (web/new-web (merge (:web conf) (:previews conf) {:commit commit}))
                          [:db :redis :elastic])))


(defn get-worker-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :notifier (notifier/new-notifier (-> conf :notifications))
    :generator (component/using (generator/new-generator conf)
                                [:db :notifier :redis])))


(defn get-web-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :web (component/using (web/new-web (merge (:web conf) (:previews conf) {:commit commit})) [:db :redis])))


(defn get-preview-worker-system [conf]
  (component/system-map
    :db (db/new-jdbc (:db conf))
    :redis (redis/new-redis (:redis conf))
    :notifier (notifier/new-notifier (-> conf :notifications))
    :preview-generator (component/using (pw-generator/new-preview-generator (:previews conf))
                                        [:db :redis :notifier])))


(def system nil)


(defn read-config [path]
  (toml/read (slurp path) :keywordize))


(defn -main [conf-path & args]
  (set-default-charset)
  (let [conf (read-config conf-path)]
    (c/set-config (merge {:commit commit} conf))
    (if (= (s/conform ::core-spec/config conf) ::s/invalid)
      (timbre/info "Bad config file!\n" (s/explain-str ::core-spec/config conf))
      (let [sys (case (:mode conf)
                  "web" (get-web-system conf)
                  "generator" (get-worker-system conf)
                  "preview-generator" (get-preview-worker-system conf)
                  (get-full-system conf))]
        (utils/init-preview-prefix (-> conf :previews :url-prefix))
        (alter-var-root #'system (constantly (component/start-system sys)))
        (notifier/application-start (:notifier system))
        system))))


(defn stop []
  (when system
    (alter-var-root #'system component/stop-system)))
