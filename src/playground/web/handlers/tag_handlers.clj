(ns playground.web.handlers.tag-handlers
  (:require
    ;; components
    [playground.db.request :as db-req]
    [playground.elastic.core :as elastic]
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
    [playground.web.handlers.constants :refer :all]
    [taoensso.timbre :as timbre]))


(defn tags-page [request]
  (tags-view/page (get-app-data request)))


(defn tag-page [request]
  (let [tag-dashed-id (-> request :route-params :*)
        page (get-pagination request)
        tag (db-req/tag-name-by-id (get-db request) {:tag tag-dashed-id})]
    (if tag
      (let [
            ;samples (db-req/samples-by-tag (get-db request) {:count  (inc samples-per-page)
            ;                                                 :offset (* samples-per-page page)
            ;                                                 :tag    tag-dashed-id})
            result (elastic/tag-samples (get-elastic request)
                                        tag
                                        (* samples-per-page page)
                                        samples-per-page)
            samples (:samples result)]
        (when (seq samples)
          (tag-view/page (merge {:result   result
                                 :page     page
                                 :tag      tag
                                 :tag-data (tags-data/get-tag-data tag)}
                                (get-app-data request)))))
      (timbre/error "Tag page: tag name by id did not found:" (pr-str tag-dashed-id)))))


(defn tag-stat-page [request]
  (tags-stat-view/page (get-app-data request)))


;; API
(defn top-tag-samples [request]
  (let [offset (-> request :params :offset)
        samples-count (-> request :params :samples-count)
        tag (-> request :params :tag)
        ;samples (time (db-req/samples-by-tag (get-db request) {:tag    tag
        ;                                                  :count  (inc samples-count)
        ;                                                  :offset offset}))
        result (elastic/tag-samples (get-elastic request)
                                    tag
                                    offset
                                    samples-count)]
    (response result)))