(ns playground.redis.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine :as car]
            [taoensso.carmine.message-queue :as car-mq]
            [taoensso.timbre :as timbre]
            [clojure.spec :as s]
            [playground.spec.redis :as redis-spec]))

(defrecord Redis [config conn]
  component/Lifecycle
  (start [this]
    (timbre/info "Redis start")
    this)
  (stop [this]
    (timbre/info "Redis stop")
    this))

(defn enqueue [redis queue message]
  (car/wcar (:conn redis)
            (car-mq/enqueue queue message)))

(defn create-worker [redis queue handler]
  (car-mq/worker (:conn redis) queue {:handler handler}))

(defn delete-worker [worker]
  (car-mq/stop worker))

(defn new-redis [config]
  {:pre [(s/valid? ::redis-spec/config config)]
   :post [(s/valid? ::redis-spec/redis %)]}
  (Redis. config
          {:spec (dissoc config :queue)
           :pool {}}))

