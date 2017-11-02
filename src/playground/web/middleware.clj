(ns playground.web.middleware
  (:require [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]
            [version-clj.core :as version-clj :refer [version-compare]]
            [playground.web.auth :as auth]
            [playground.web.utils :as web-utils]))

;; =====================================================================================================================
;; Repo, version middleware for projects info
;; =====================================================================================================================
(defn check-repo-middleware [handler]
  (fn [request]
    (let [repo-name (-> request :route-params :repo)
          repo (db-req/repo-by-name (get-db request) {:name repo-name})]
      (when repo
        (handler (assoc-in request [:app :repo] repo))))))

(defn check-version-middleware [handler]
  (fn [request]
    (let [repo (get-repo request)
          version-name (-> request :route-params :version)]
      (let [version (if version-name
                      (db-req/version-by-name (get-db request) {:repo-id (:id repo)
                                                                :name    version-name})
                      (db-req/last-version (get-db request) {:repo-id (:id repo)}))]
        (when version
          (handler (assoc-in request [:app :version] version)))))))

;; =====================================================================================================================
;; Samples middlewares
;; =====================================================================================================================
(defn check-sample-middleware [handler]
  (fn [request]
    (let [repo (get-repo request)
          version (get-version request)
          sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version-id (:id version)
                                                         :url        sample-url})
          full-sample (-> sample
                          (assoc :repo-name (:name repo))
                          (assoc :repo-title (:title repo))
                          (assoc :version-name (:name version))
                          db-req/add-full-url)]
      (when sample
        (handler (assoc-in request [:app :sample] full-sample))))))

(defn check-last-user-sample-middleware [handler]
  (fn [request]
    (let [hash (-> request :route-params :hash)
          sample (db-req/last-sample-by-hash (get-db request) {:url hash})]
      (when sample
        (handler (assoc-in request [:app :sample] sample))))))

(defn check-user-sample-middleware [handler]
  (fn [request]
    (let [hash (-> request :route-params :hash)
          version (try (-> request :route-params :version Integer/parseInt) (catch Exception _ 0))
          sample (db-req/sample-by-hash (get-db request) {:url     hash
                                                          :version version})]
      (when sample
        (handler (assoc-in request [:app :sample] sample))))))

(defn check-template-middleware [handler]
  (fn [request]
    (let [template-url (-> request :params :template)
          sample (if template-url
                   (db-req/template-by-url (get-db request) {:url template-url})
                   web-utils/empty-sample)
          new-sample (assoc sample :new true)]
      (when sample
        (handler (assoc-in request [:app :sample] new-sample))))))

;; =====================================================================================================================
;; Middlewares for all repos, templates,
;; tags for footer and tags for tags-page
;; =====================================================================================================================
(defn templates-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :templates]
                       (db-req/templates (get-db request))))))

(defn repos-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :repos]
                       (db-req/repos (get-db request))))))

(defn tags-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :tags]
                       (db-req/top-tags (get-db request) {:limit 7})))))

(defn all-tags-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :all-tags]
                       (db-req/tags (get-db request))))))

(defn data-sets-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :data-sets]
                       (db-req/top-data-sets (get-db request) {:limit 7})))))

(defn all-data-sets-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :all-data-sets]
                       (db-req/data-sets (get-db request))))))

(defn pagination-page-middleware [handler]
  (fn [request]
    (let [page-param (-> request :params :page)
          page (if page-param
                 (try (-> request :params :page Integer/parseInt dec)
                      (catch Exception _ -1))
                 0)]
      (when (>= page 0)
        (handler (assoc-in request [:app :page] page))))))

;; =====================================================================================================================
;; Aggregation functions
;; =====================================================================================================================
(defn base-page-middleware [handler & [action]]
  (let [check-perm-fn (if action
                        (fn [handler] (auth/permissions-middleware handler action))
                        identity)]
    (-> handler
        templates-middleware
        repos-middleware
        tags-middleware
        data-sets-middleware
        check-perm-fn
        auth/check-anonymous-middleware)))

(defn repo-sample [handler]
  (-> handler
      check-sample-middleware
      check-version-middleware
      check-repo-middleware
      auth/check-anonymous-middleware))

(defn last-user-sample [handler]
  (-> handler
      check-last-user-sample-middleware
      auth/check-anonymous-middleware))

(defn user-sample [handler]
  (-> handler
      check-user-sample-middleware
      auth/check-anonymous-middleware))