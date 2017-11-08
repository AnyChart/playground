(ns playground.site.pages.landing-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.landing-page-utils :as landing-page-utils]))

;;======================================================================================================================
;; Landing page
;;======================================================================================================================
(def *page (atom 0))
(def *is-end (atom false))

(declare load-popular-samples)

(defn on-popular-samples-load [data]
  (dom/removeChildren (dom/getElement "popular-samples"))
  (set! (.-innerHTML (.getElementById js/document "popular-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (landing-page-utils/title @*page))
  (update-buttons "popular-samples-prev"
                  "popular-samples-next"
                  *page
                  @*is-end
                  "/?page="
                  load-popular-samples))


(defn load-popular-samples []
  (POST "/landing-samples.json"
        {:params        {:offset (* samples-per-landing @*page)}
         :handler       on-popular-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startLanding [_end _page]
  (reset! *is-end _end)
  (reset! *page _page)
  (init-buttons "popular-samples-prev"
                "popular-samples-next"
                *page
                load-popular-samples))

;;======================================================================================================================
;; Landing page: tag block
;;======================================================================================================================

;(defn on-landing-tag-samples-load [data]
;  (dom/removeChildren (dom/getElement "popular-tags"))
;  (set! (.-innerHTML (.getElementById js/document "popular-tags"))
;        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
;  (reset! *landing-tag-samples-is-end (:end data))
;  (.pushState (.-history js/window) nil nil (str "?samples=" (inc @*popular-samples-page)
;                                                 "&tags=" (inc @*landing-tag-samples-page)))
;  (set-buttons-visibility "popular-tags-prev"
;                          "popular-tags-next"
;                          @*landing-tag-samples-page
;                          @*landing-tag-samples-is-end
;                          "tags"))
;
;
;(defn load-landing-tag-samples []
;  (POST "/landing-tag-samples.json"
;        {:params        {:offset (* samples-per-block @*landing-tag-samples-page)}
;         :handler       on-landing-tag-samples-load
;         :error-handler #(utils/log "Error!" %)}))
;
;
;(defn ^:export startLandingTag [_end _page]
;  (reset! *landing-tag-samples-is-end _end)
;  (reset! *landing-tag-samples-page _page)
;  (init-buttons "popular-tags-prev"
;                "popular-tags-next"
;                *landing-tag-samples-page
;                load-landing-tag-samples)
;  (set-buttons-visibility "popular-tags-prev"
;                          "popular-tags-next"
;                          @*landing-tag-samples-page
;                          @*landing-tag-samples-is-end
;                          "tags"))
