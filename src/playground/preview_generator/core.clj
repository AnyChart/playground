(ns playground.preview-generator.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]]
            [me.raynes.fs :as fs]
            [playground.notification.slack :as slack]
            [playground.redis.core :as redis]
            [playground.preview-generator.phantom :as phantom]
            [playground.preview-generator.phantom-embed :as phantom-embed]
            [playground.preview-generator.sample-preparation :as sample-prep]
            [playground.db.request :as db-req]
            [com.climate.claypoole :as cp]))

(declare generate-previews)

(defn message-handler [generator]
  (fn [{:keys [message attemp]}]
    (timbre/info "Preview generator redis message: " (if (seq message)
                                                       (str (count message) " - first 10: " (pr-str (take 10 message)))
                                                       message))
    (when (seq message)
      (generate-previews generator message))
    {:status :success}))


(defrecord Generator [conf db redis notifier]
  component/Lifecycle

  (start [this]
    (timbre/info "Preview generator start" conf)
    (let [drivers (phantom-embed/create-drivers)
          drivers-queue (phantom-embed/setup-queue drivers)
          this (assoc this :drivers drivers
                           :drivers-queue drivers-queue)]
      (assoc this
        :redis-worker (redis/create-worker redis (-> redis :config :preview-queue) (message-handler this)))))

  (stop [this]
    (timbre/info "Preview generator stop")
    (redis/delete-worker (:redis-worker this))
    (dissoc this :conf)))


(defn new-preview-generator [conf]
  (map->Generator {:conf conf}))


(defn generate-previews [generator ids]
  (let [samples (db-req/samples-by-ids (:db generator) {:ids (db-req/raw-coll ids)})]
    (timbre/info "Generate previews: " (if (= 1 (count samples))
                                         (-> samples first :name)
                                         (count ids)))
    (fs/mkdirs (-> generator :conf :images-dir))
    (let [
          ;result (doall (pmap #(phantom/generate-img (-> generator :conf :phantom-engine)
          ;                                           (-> generator :conf :generator)
          ;                                           (-> generator :conf :images-dir)
          ;                                           %) samples))
          result (doall (cp/pmap 4
                                 #(phantom-embed/generate-image (sample-prep/prepare-sample %) generator)
                                 samples))
          good-results (filter (complement :error) result)
          result-ids (map :id good-results)
          bad-results (filter :error result)]
      (when (seq bad-results)
        (timbre/info "Bad results: " (count bad-results) "/" (count ids))
        (doseq [er (take 20 result-ids)]
          (timbre/info "Bad result: " (:id er) (:url er) (:error er))))
      (when (seq result-ids)
        (db-req/update-samples-preview! (:db generator) {:ids     (db-req/raw-coll result-ids)
                                                         :preview true}))
      (timbre/info "End generate previews: " (if (= 1 (count samples))
                                               (-> samples first :name)
                                               (str (count result-ids) " from " (count ids)))))))