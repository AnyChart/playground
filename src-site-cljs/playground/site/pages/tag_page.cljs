(ns playground.site.pages.tag-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.tag-page-utils :as tag-page-utils]))

;;======================================================================================================================
;; Tags page
;;======================================================================================================================
(def *page (atom 0))
(def *is-end (atom false))
(def *tag (atom nil))
(def *loading (atom false))

(declare load-tag-samples)

(defn on-tag-samples-load [data]
  (dom/removeChildren (dom/getElement "tag-samples"))
  (set! (.-innerHTML (.getElementById js/document "tag-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (tag-page-utils/title @*tag @*page))
  (update-buttons "tag-samples-prev"
                  "tag-samples-next"
                  *page
                  @*is-end
                  (str "/tags/" @*tag "?page=")
                  load-tag-samples
                  *loading))


(defn load-tag-samples []
  (POST "/tag-samples.json"
        {:params        {:offset        (* samples-per-page @*page)
                         :tag           @*tag
                         :samples-count samples-per-page}
         :handler       on-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startTagPage [_end _page _tag]
  ;(utils/log "Start tag page: " _end _page _tag)
  (reset! *is-end _end)
  (reset! *page _page)
  (reset! *tag _tag)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *page
                load-tag-samples
                *loading))
