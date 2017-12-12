(ns playground.web.handlers.tag-handlers
  (:require [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
    ;; views
            [playground.views.tag.tags-page :as tags-view]
            [playground.views.tag.tag-page :as tag-view]
            [playground.views.tag.tags-stat-page :as tags-stat-view]
    ;; data
            [playground.data.tags :as tags-data]
    ;; consts
            [playground.web.handlers.constants :refer :all]))


(defn tags-page [request]
  (tags-view/page (get-app-data request)))


(defn tag-page [request]
  (let [tag-dashed-id (-> request :route-params :*)
        tag (db-req/tag-name-by-id (get-db request) {:tag tag-dashed-id})
        page (get-pagination request)
        samples (db-req/samples-by-tag (get-db request) {:count  (inc samples-per-page)
                                                         :offset (* samples-per-page page)
                                                         :tag    tag-dashed-id})]
    (when (and tag (seq samples))
      (tag-view/page (merge {:samples  (take samples-per-page samples)
                             :end      (< (count samples) (inc samples-per-page))
                             :page     page
                             :tag      tag
                             :tag-data (tags-data/get-tag-data tag)}
                            (get-app-data request))))))


(defn tag-stat-page [request]
  (tags-stat-view/page (get-app-data request)))

;; API
(defn top-tag-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        samples-count (-> request :params :samples-count)
        tag (-> request :params :tag)
        samples (db-req/samples-by-tag (get-db request) {:tag    tag
                                                         :count  (inc samples-count)
                                                         :offset offset})
        result {:samples (take samples-count samples)
                :end     (< (count samples) (inc samples-count))}]
    (response result)))