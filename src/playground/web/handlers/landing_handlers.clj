(ns playground.web.handlers.landing-handlers
  (:require
    ;; comp
    [playground.db.request :as db-req]
    [playground.elastic.core :as elastic]
    ;; web
    [playground.web.helpers :refer :all]
    [playground.web.handlers.constants :refer :all]
    [playground.web.utils :as web-utils :refer [response]]
    ;; views
    [playground.views.landing.landing-page :as landing-view]))


;; =====================================================================================================================
;; Pages
;; =====================================================================================================================

(defn landing-page [request]
  (let [samples-page (get-pagination request)
        ;samples (db-req/top-samples (get-db request) {:count  (inc samples-per-landing)
        ;                                              :offset (* samples-per-landing samples-page)})
        ;result {:samples (take samples-per-landing samples)
        ;        :end     (< (count samples) (inc samples-per-landing))}
        result (elastic/top-samples (get-elastic request)
                                    (* samples-per-landing samples-page)
                                    samples-per-landing)]
    (response (landing-view/page (merge (get-app-data request)
                                        {:result result
                                         :page   samples-page})))))


;; =====================================================================================================================
;; API
;; =====================================================================================================================
(defn top-landing-samples [request]
  (let [offset (-> request :params :offset)
        result (elastic/top-samples (get-elastic request)
                                    offset
                                    samples-per-landing)
        ;samples (db-req/top-samples (get-db request) {:count  (inc samples-per-landing)
        ;                                              :offset offset})
        ;result {:samples (take samples-per-landing samples)
        ;        :end     (< (count samples) (inc samples-per-landing))}
        ]
    (response result)))

;(defn top-landing-tag-samples [request]
;  (let [offset* (-> request :params :offset)
;        offset (if (int? offset*) offset* (Integer/parseInt offset*))
;        samples (db-req/get-top-tags-samples (get-db request) {:count  (inc samples-per-block)
;                                                               :offset offset})
;        result {:samples (take samples-per-block samples)
;                :end     (< (count samples) (inc samples-per-block))}]
;    (response result)))
