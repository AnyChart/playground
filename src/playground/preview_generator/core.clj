(ns playground.preview-generator.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]]
            [me.raynes.fs :as fs]
            [playground.notification.slack :as slack]
            [playground.redis.core :as redis]
            [playground.preview-generator.phantom :as phantom]
            [playground.db.request :as db-req]))

(declare generate-previews)

(defn message-handler [generator]
  (fn [{:keys [message attemp]}]
    (timbre/info "Preview generator redis message: " message)
    (when (seq message)
      ;(generate-previews generator message)
      )
    {:status :success}))


(defrecord Generator [conf db redis notifier]
  component/Lifecycle

  (start [this]
    (timbre/info "Preview generator start" conf)
    (assoc this
      :redis-worker (redis/create-worker redis (-> redis :config :preview-queue) (message-handler this))))

  (stop [this]
    (timbre/info "Preview generator stop")
    (redis/delete-worker (:redis-worker this))
    (dissoc this :conf)))


(defn new-preview-generator [conf]
  (map->Generator {:conf conf}))


(defn generate-previews [generator ids]
  (let [samples (db-req/samples-by-ids (:db generator) {:ids ids})]
    (timbre/info "Generate previews: " (if (= 1 (count samples)) (-> samples first :name) (count ids)))
    (fs/mkdirs (-> generator :conf :images-dir))
    (let [result (doall (pmap #(phantom/generate-img (-> generator :conf :phantom-engine)
                                                     (-> generator :conf :generator)
                                                     (-> generator :conf :images-dir)
                                                     %) samples))
          good-results (filter (complement :error) result)
          ids (map :id good-results)]
      (when (seq ids)
        (db-req/update-samples-preview! (:db generator) {:ids ids :preview true})))
    (timbre/info "End generate previews: " (if (= 1 (count samples)) (-> samples first :name) (count ids)))))