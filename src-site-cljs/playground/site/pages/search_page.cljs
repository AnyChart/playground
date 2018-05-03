(ns playground.site.pages.search-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.tag-page-utils :as tag-page-utils]
            [playground.data.tags :as tags-data]))


;;======================================================================================================================
;; Search page
;;======================================================================================================================
(def *page (atom 0))
(def *is-end (atom false))
(def *q (atom nil))
(def *loading (atom false))

(declare load-search-samples)

(defn on-search-samples-load [data]
  (dom/removeChildren (dom/getElement "search-samples"))
  (set! (.-innerHTML (.getElementById js/document "search-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?q=" @*q "&page=" (inc @*page)))
  (change-title (tag-page-utils/title @*q @*page))
  (update-buttons "search-samples-prev"
                  "search-samples-next"
                  *page
                  @*is-end
                  (str "/search?q=" @*q "&page=")
                  load-search-samples
                  *loading))


(defn load-search-samples []
  (POST "/search"
        {:params        {:offset        (* samples-per-page @*page)
                         :q             @*q
                         :samples-count samples-per-page}
         :handler       on-search-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startSearchPage [_end _page _q]
  ;(utils/log "Start tag page: " _end _page _q)
  (reset! *is-end _end)
  (reset! *page _page)
  (reset! *q _q)
  (init-buttons "search-samples-prev"
                "search-samples-next"
                *page
                load-search-samples
                *loading))