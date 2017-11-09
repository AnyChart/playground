(ns playground.web.handlers.dataset-handlers
  (:require [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
    ;; views
            [playground.views.data-set.data-sets-page :as data-sets-view]
            [playground.views.data-set.data-set-page :as data-set-view]))


(defn data-sets-page [request]
  (let [page (get-pagination request)
        all-datasets (:all-data-sets (get-app-data request))
        page-datasets (data-sets-view/page-datasets page (get-app-data request))
        is-end (data-sets-view/is-end (count all-datasets) page)]
    (when (seq page-datasets)
      (data-sets-view/page (get-app-data request) is-end page page-datasets))))


(defn data-set-page [request]
  (let [data-source-name (or (-> request :params :data-source)
                             (-> (db-req/data-sources (get-db request)) first :name))
        data-set-name (-> request :params :data-set)
        data-set (db-req/data-set-by-name (get-db request) {:data-source-name data-source-name
                                                            :name             data-set-name})]
    (when data-set
      (data-set-view/page (merge (get-app-data request)
                                 {:data-set data-set})))))
