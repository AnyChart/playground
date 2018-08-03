(ns playground.site.pages.tag-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title update-pagination
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.tag-page-utils :as tag-page-utils]
            [playground.data.tags :as tags-data]
            [clojure.string :as string]))

;;======================================================================================================================
;; Tags page
;;======================================================================================================================
(def *page (atom 0))
(def *max-page (atom 0))
(def *is-end (atom false))

(def *tag (atom nil))
(def *loading (atom false))

(declare load-tag-samples)


(defn on-tag-samples-load [data]
  (dom/removeChildren (dom/getElement "tag-samples"))
  (set! (.-innerHTML (.getElementById js/document "tag-samples"))
        (string/join (map #(h/html %) (sample-view/samples (:samples data)))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (tag-page-utils/title @*tag @*page))
  (update-buttons "tag-samples-prev"
                  "tag-samples-next"
                  *page
                  @*is-end
                  (str "/tags/" (tags-data/original-name->id-name @*tag) "?page=")
                  load-tag-samples
                  *loading)
  (update-pagination *page *max-page *loading (str "/tags/" (tags-data/original-name->id-name @*tag) "?page=") load-tag-samples))


(defn load-tag-samples []
  (POST "/tag-samples.json"
        {:params        {:offset        (* samples-per-page @*page)
                         :tag           @*tag
                         :samples-count samples-per-page}
         :handler       on-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startTagPage [_page _max-page _end _tag]
  ;(utils/log "Start tag page: " _page _max-page _end _tag)
  (reset! *page _page)
  (reset! *max-page _max-page)
  (reset! *is-end _end)
  (reset! *tag _tag)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *page
                load-tag-samples
                *loading)
  (update-pagination *page *max-page *loading (str "/tags/" (tags-data/original-name->id-name @*tag) "?page=") load-tag-samples))
