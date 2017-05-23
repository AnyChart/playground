(ns playground.web.middleware
  (:require [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]
            [version-clj.core :as version-clj :refer [version-compare]]
            [playground.web.auth :as auth]))

;; middleware for getting repo, version, sample, all repos, templates,
;; tags for footer and tags for tags-page
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

(defn check-sample-middleware [handler]
  (fn [request]
    (let [repo (get-repo request)
          version (get-version request)
          sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version-id (:id version)
                                                         :url        sample-url})
          full-sample (-> sample
                          (assoc :repo-name (:name repo))
                          (assoc :version-name (:name version))
                          db-req/add-full-url)]
      (when sample
        (handler (assoc-in request [:app :sample] full-sample))))))

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

(defn base-page-middleware [handler]
  (-> handler
      templates-middleware
      repos-middleware
      tags-middleware
      data-sets-middleware
      auth/check-anonymous-middleware))