(ns playground.site.pages.chart-types-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]))


;;======================================================================================================================
;; Chart types page: not used now
;;======================================================================================================================
;(def *chart-types-page (atom 0))
;(def *chart-types-is-end (atom false))
;(def ^:const chart-types-count 25)
;
;(defn is-end [all-items-count on-page-count page]
;  (let [pages (int (.ceil js/Math (/ all-items-count on-page-count)))]
;    (>= page (dec pages))))
;
;
;(defn chart-types-buttons-click []
;  (let [box (first (array-seq (.getElementsByClassName js/document "chart-type-container")))
;        els (array-seq (.-childNodes box))]
;    (dotimes [i (count els)]
;      (if (and
;            (>= i (* @*chart-types-page 25))
;            (< i (+ 25 (* @*chart-types-page 25))))
;        (set! (.-display (.-style (nth els i))) "block")
;        (set! (.-display (.-style (nth els i))) "none")))
;    (reset! *chart-types-is-end (is-end (count els) 25 @*chart-types-page)))
;  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*chart-types-page)))
;  (set-buttons-visibility "tag-samples-prev"
;                          "tag-samples-next"
;                          @*chart-types-page
;                          @*chart-types-is-end
;                          "page"))
;
;
;(defn ^:export startChartTypesPage [_end _page]
;  (reset! *chart-types-page _page)
;  (reset! *chart-types-is-end _end)
;  (init-buttons "tag-samples-prev"
;                "tag-samples-next"
;                *chart-types-page
;                chart-types-buttons-click)
;  (set-buttons-visibility "tag-samples-prev"
;                          "tag-samples-next"
;                          @*chart-types-page
;                          @*chart-types-is-end
;                          "page"))
