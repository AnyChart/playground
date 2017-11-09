(ns playground.web.handlers.chart-type-handlers
  (:require [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
    ;; consts
            [playground.web.handlers.constants :refer :all]
    ;; views
            [playground.views.chart-type.chart-types-page :as chart-types-view]
            [playground.views.chart-type.chart-type-page :as chart-type-view]
            [playground.views.chart-type.chart-types-categories-page :as chart-type-categories-view]
            [playground.views.chart-type.chart-types-category-page :as chart-type-category-view]
    ;; data
            [playground.web.chartopedia :as chartopedia]))


(defn chart-types-page [request]
  (let [chart-types chartopedia/chart-types]
    (chart-types-view/page (get-app-data request) chart-types)))


(defn chart-type-page [request]
  (let [chart-name (-> request :params :chart-type)]
    (when-let [chart-type (chartopedia/get-chart chart-name)]
      (let [tag (:name chart-type)
            page (get-pagination request)
            samples (db-req/samples-by-tag (get-db request) {:count  (inc samples-per-block)
                                                             :offset (* samples-per-block page)
                                                             :tag    tag})]
        (when (seq samples)
          (chart-type-view/page (merge {:samples (take samples-per-block samples)
                                        :end     (< (count samples) (inc samples-per-block))
                                        :page    page
                                        :tag     tag}
                                       (get-app-data request))
                                chart-type
                                (chartopedia/get-relations chart-type)))))))

(defn chart-types-categories-page [request]
  (chart-type-categories-view/page (get-app-data request) chartopedia/categories))

(defn chart-types-category-page [request]
  (let [category-name (-> request :params :category)]
    (when-let [category (chartopedia/get-category category-name)]
      (chart-type-category-view/page (get-app-data request) category))))