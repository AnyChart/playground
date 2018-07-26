(ns playground.web.handlers.admin-handlers
  (:require [playground.views.admin.admin-page :as admin-view]
            [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]
            [taoensso.timbre :as timbre]
            [playground.db.actions :as db-actions]
            [playground.web.utils :as web-utils]
            [playground.redis.core :as redis]))


;; Page
(defn page [request]
  (let [data (:app request)]
    (admin-view/page data)))


;; API
(defn versions [request]
  (let [project (-> request :params :project)
        versions (db-req/versions-by-repo-name (get-db request) {:name project})]
    versions))


(defn delete-version [request]
  (let [project (-> request :params :project)
        version (-> request :params :version)
        repo (db-req/repo-by-name (get-db request) {:name project})
        branch (db-req/version-by-name (get-db request) {:repo-id (:id repo)
                                                         :name    version})]
    (timbre/info "Delete version request: " project version)
    (db-actions/remove-branch (get-db request) branch)
    (web-utils/response {:status :ok})))


(defn rebuild-version [request]
  (let [project (-> request :params :project)
        version (-> request :params :version)
        repo (db-req/repo-by-name (get-db request) {:name project})
        branch (db-req/version-by-name (get-db request) {:repo-id (:id repo)
                                                         :name    version})]
    (timbre/info "Rebuild version request: " project version)
    (db-actions/remove-branch (get-db request) branch)
    (redis/enqueue (get-redis request)
                   (get-redis-queue request)
                   {:repo    project
                    :version version})
    (web-utils/response {:status :ok})))