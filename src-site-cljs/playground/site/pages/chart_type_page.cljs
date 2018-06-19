(ns playground.site.pages.chart-type-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title update-pagination
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.chart-type-page-utils :as chart-type-page-utils]))

;;======================================================================================================================
;; Chart type tags page
;;======================================================================================================================
(def *page (atom 0))
(def *max-page (atom 0))
(def *is-end (atom false))

(def *tag (atom nil))
(def *chart-type-name (atom nil))
(def *chart-type-id (atom nil))
(def *loading (atom false))

(declare load-tag-samples)

(defn on-tag-samples-load [data]
  (dom/removeChildren (dom/getElement "tag-samples"))
  (set! (.-innerHTML (.getElementById js/document "tag-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (chart-type-page-utils/title @*chart-type-name @*page))
  (update-buttons "tag-samples-prev"
                  "tag-samples-next"
                  *page
                  @*is-end
                  (str "/chart-types/" @*chart-type-id "?page=")
                  load-tag-samples
                  *loading)
  (update-pagination *page *max-page *loading (str "/chart-types/" @*chart-type-id "?page=") load-tag-samples))


(defn load-tag-samples []
  (POST "/tag-samples.json"
        {:params        {:offset        (* samples-per-block @*page)
                         :tag           @*tag
                         :samples-count samples-per-block}
         :handler       on-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startChartTypePage [_page _max-page _end _tag _chart-type-id _chart-type-name]
  ;(utils/log "Start tag page: " _page _max-page _end  _tag _chart-type-id)
  (reset! *page _page)
  (reset! *max-page _max-page)
  (reset! *is-end _end)
  (reset! *tag _tag)
  (reset! *chart-type-id _chart-type-id)
  (reset! *chart-type-name _chart-type-name)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *page
                load-tag-samples
                *loading)
  (update-pagination *page *max-page *loading (str "/chart-types/" @*chart-type-id "?page=") load-tag-samples))