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
            [com.climate.claypoole :as cp]
            [clojure.string :as string]
            [playground.utils.utils :as utils]
            [playground.preview-generator.download :as download])
  (:import (com.maxcdn MaxCDN)))


;; =====================================================================================================================
;; Purge MaxCDN images cache
;; =====================================================================================================================
(defn purge-files [files generator]
  (let [maxcdn-conf (-> generator :conf :maxcdn)
        zone-id (:zone-id maxcdn-conf)
        api (MaxCDN. (:alias maxcdn-conf) (:key maxcdn-conf) (:secret maxcdn-conf))
        files-query (string/join "&" (map #(str "file=" %) files))
        ;; maxcdn query should be less than ~ 21517 char count
        query (str "/zones/pull.json/" zone-id "/cache?" files-query)]
    (timbre/info "Query length: " (count query))
    (try
      (let [data (.delete api query)
            code (.code data)
            error (.error data)
            error-message (.getErrorMessage data)]
        (if error
          (timbre/info "Purge files error: " error-message)
          (timbre/info "Purge files:" (count files) "ok!")))
      (catch Exception e
        (timbre/info "Purge files exception: " e)))))


(defn purge-cache [generator samples good-results]
  (let [
        ;; we need to purge maxcdn cache only for generated repo samples
        samples (filter (fn [sample]
                          (and (:version-id sample)
                               (some (fn [gen-sample]
                                       (= (:id gen-sample) (:id sample)))
                                     good-results)))
                        samples)
        ;; images names for purging must be e.g.:
        ;; /pg/gallery-8.1.0-some-name.png for "com"
        ;; /stg/gallery-8.1.0-some-name.png for "stg"
        image-names (map (fn [sample]
                           (str (-> generator :conf :cdn-prefix)
                                (utils/image-name sample)))
                         samples)]
    (when (seq image-names)
      ;; TODO: delete test
      (timbre/info "Purge cache:" (count image-names) (pr-str (take 3 image-names)))
      ;; maxcdn query should be less than ~21517 char count
      (let [groups (partition-all 150 image-names)]
        (doseq [names groups]
          (purge-files names generator))))))


;; =====================================================================================================================
;; Generate previews
;; =====================================================================================================================
(defn generate-previews [generator ids]
  (let [samples (db-req/samples-by-ids (:db generator) {:ids (db-req/raw-coll ids)})]
    (timbre/info "Generate previews: " (if (= 1 (count samples))
                                         (-> samples first :name)
                                         (count ids)))
    (fs/mkdirs (-> generator :conf :images-dir))
    (download/clear)
    (let [
          ;result (doall (pmap #(phantom/generate-img (-> generator :conf :phantom-engine)
          ;                                           (-> generator :conf :generator)
          ;                                           (-> generator :conf :images-dir)
          ;                                           %) samples))
          result (doall (cp/pmap phantom-embed/drivers-count
                                 #(phantom-embed/generate-image (sample-prep/prepare-sample %) generator)
                                 samples))
          good-results (filter (complement :error) result)
          result-ids (map :id good-results)
          bad-results (filter :error result)]

      (when (seq bad-results)
        (timbre/info "Bad results: " (count bad-results) "/" (count ids))
        (doseq [er (take 10 bad-results)]
          (timbre/info "Bad result: " (:id er) (:url er) (:error er))))

      (when (seq result-ids)
        (db-req/update-samples-preview! (:db generator) {:ids     (db-req/raw-coll result-ids)
                                                         :preview true}))

      (timbre/info "End generate previews: " (count result-ids) "from" (count ids) " : " (pr-str (take 3 good-results)))

      (when (-> generator :conf :cdn-purge)
        (purge-cache generator samples good-results)))))


;; =====================================================================================================================
;; Redis message handler
;; =====================================================================================================================
(defn message-handler [generator]
  (fn [{:keys [message attemp]}]
    (timbre/info "Preview generator redis message: " (if (seq message)
                                                       (str (count message) " - first 10: " (pr-str (take 10 message)))
                                                       message))
    (when (seq message)
      (generate-previews generator message))
    {:status :success}))


;; =====================================================================================================================
;; Preview generator component
;; =====================================================================================================================
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