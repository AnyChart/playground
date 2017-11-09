(ns playground.web.handlers.repo-handlers
  (:require [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
    ;; views
            [playground.views.repo.repos-page :as repos-view]
            [playground.views.repo.repo-page :as repo-view]
            [playground.views.repo.version-page :as version-view]
    ;; consts
            [playground.web.handlers.constants :refer :all]))


(defn repos-page [request]
  (repos-view/page (get-app-data request)))


(defn repo-page [request]
  (let [repo (get-repo request)
        versions (db-req/versions (get-db request) {:repo-id (:id repo)})
        versions-with-samples (filter (comp pos? :samples-count) versions)]
    (repo-view/page (merge (get-app-data request)
                           {:repo     repo
                            :versions versions-with-samples}))))


(defn version-page [request]
  (let [repo (get-repo request)
        version (get-version request)
        page (get-pagination request)
        samples (db-req/samples-by-version (get-db request) {:version_id (:id version)
                                                             :offset     (* samples-per-page page)
                                                             :count      (inc samples-per-page)})]
    (when (seq samples)
      (version-view/page (merge (get-app-data request)
                                {:samples (take samples-per-page samples)
                                 :end     (< (count samples) (inc samples-per-page))
                                 :page    page
                                 :version version
                                 :repo    repo})))))

;; API
(defn top-version-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        version-id (-> request :params :version_id)
        samples (db-req/samples-by-version (get-db request) {:version_id version-id
                                                             :count      (inc samples-per-page)
                                                             :offset     offset})
        result {:samples (take samples-per-page samples)
                :end     (< (count samples) (inc samples-per-page))}]
    (response result)))