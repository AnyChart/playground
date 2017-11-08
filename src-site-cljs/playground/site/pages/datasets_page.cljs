(ns playground.site.pages.datasets-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title
                                             init-buttons set-buttons-visibility]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.datasets-page-utils :as datasets-page-utils]))

;;======================================================================================================================
;; Datasets page
;;======================================================================================================================
(def *page (atom 0))
(def *is-end (atom false))
(def ^:const datasets-count 6)

(defn is-end [all-items-count on-page-count page]
  (let [pages (int (.ceil js/Math (/ all-items-count on-page-count)))]
    (>= page (dec pages))))

(defn datasets-buttons-click []
  (let [box (first (array-seq (.getElementsByClassName js/document "datasets-container")))
        els (array-seq (.-childNodes box))]
    (dotimes [i (count els)]
      (if (and
            (>= i (* @*page 6))
            (< i (+ 6 (* @*page 6))))
        (set! (.-display (.-style (nth els i))) "block")
        (set! (.-display (.-style (nth els i))) "none")))
    (reset! *is-end (is-end (count els) 6 @*page)))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (datasets-page-utils/title @*page))
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*page
                          @*is-end
                          "page"))

(defn ^:export startDatasetsPage [_end _page]
  (reset! *page _page)
  (reset! *is-end _end)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *page
                datasets-buttons-click)
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*page
                          @*is-end
                          "page"))
