(ns playground.web.handlers.sample.api
  (:require
    ;; comp
    [playground.db.request :as db-req]
    [playground.redis.core :as redis]
    ;; handlers
    [playground.web.handlers.sample.pages :as sample-handlers]
    ;; web
    [playground.web.helpers :refer :all]
    [playground.web.utils :as web-utils :refer [response]]
    ;; misc
    [clojure.string :as string]))

(defn run [request]
  (let [code (-> request :params :code)
        style (-> request :params :style)
        markup (-> request :params :markup)
        styles (-> request :params :styles (string/split #","))
        scripts (-> request :params :scripts (string/split #","))
        data {:name              "Default name"
              :tags              []
              :short-description "Default short desc"

              :scripts           scripts
              :styles            styles

              :markup            markup
              :code              code
              :style             style}]
    ;(response (render-file "templates/sample.selmer" data))
    (sample-handlers/show-sample-iframe-by-sample data)))


(defn fork [request]
  ;(prn "Fork: " (-> request :session :user) (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (web-utils/new-hash)
        sample* (assoc sample
                  :url hash
                  :version 0
                  :owner-id (-> request :session :user :id))]
    (let [id (db-req/add-sample! (get-db request) sample*)]
      (db-req/update-version-user-samples-latest! (get-db request) {:latest  true
                                                                    :url     hash
                                                                    :version 0})
      (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
    (response {:status   :ok
               :hash     hash
               :version  0
               :owner-id (:id (get-user request))})))


(defn save [request]
  ;(prn "Save: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (:url sample)
        db-sample (when (and hash (seq hash))
                    (db-req/sample-template-by-url (get-db request) {:url hash}))]
    (if (and db-sample
             (nil? (:template-id db-sample))
             (nil? (:version-id db-sample))
             (= (:id (get-user request)) (:owner-id db-sample)))
      (let [version (:version db-sample)
            new-version (inc version)
            sample* (assoc sample :version new-version
                                  :owner-id (-> request :session :user :id))]
        (let [id (db-req/add-sample! (get-db request) sample*)]
          (db-req/update-all-user-samples-latest! (get-db request) {:latest  false
                                                                    :url     hash
                                                                    :version new-version})
          (db-req/update-version-user-samples-latest! (get-db request) {:latest  true
                                                                        :url     hash
                                                                        :version new-version})
          (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
        (response {:status   :ok
                   :hash     hash
                   :version  new-version
                   :owner-id (:id (get-user request))}))
      (fork request))))