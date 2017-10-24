(ns playground.site.landing
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [playground.views.sample :as sample-view]
            [ajax.core :refer [GET POST]]
            [hiccups.runtime :as hiccupsrt]
            [clojure.string :as s]))

;;======================================================================================================================
;; Main consts and funcs
;;======================================================================================================================
(def ^:const samples-per-page 12)
(def ^:const samples-per-landing 9)
(def ^:const samples-per-block 6)


(defn set-buttons-visibility [prev-btn-name
                              next-btn-name
                              page
                              end
                              url-param]
  ;(utils/log "set visibitlity: " prev-btn-name next-btn-name page end url-param)
  (let [prev-button (dom/getElement prev-btn-name)
        next-button (dom/getElement next-btn-name)]
    (style/setElementShown prev-button (pos? page))
    (.setAttribute prev-button "href" (str "/?" url-param "=" page))
    (.setAttribute prev-button "title" (str "Prev page, " page))
    (style/setElementShown next-button (not end))
    (.setAttribute next-button "href" (str "/?" url-param "=" (inc (inc page))))
    (.setAttribute next-button "title" (str "Next page, " (inc (inc page))))))


(defn init-buttons [prev-btn-name
                    next-btn-name
                    *page
                    load-samples-fn]
  (let [prev-button (dom/getElement prev-btn-name)
        next-button (dom/getElement next-btn-name)]
    (event/listen prev-button "click" (fn [e]
                                        (.preventDefault e)
                                        (swap! *page dec)
                                        (load-samples-fn)))
    (event/listen next-button "click" (fn [e]
                                        (.preventDefault e)
                                        (swap! *page inc)
                                        (load-samples-fn)))))

;;======================================================================================================================
;; Landing page
;;======================================================================================================================
(def *popular-samples-page (atom 0))
(def *popular-samples-is-end (atom false))

;(def *landing-tag-samples-page (atom 0))
;(def *landing-tag-samples-is-end (atom false))

(defn on-popular-samples-load [data]
  (dom/removeChildren (dom/getElement "popular-samples"))
  (set! (.-innerHTML (.getElementById js/document "popular-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *popular-samples-is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?samples=" (inc @*popular-samples-page)
                                                 ;"&tags=" (inc @*landing-tag-samples-page)
                                                 ))
  (set-buttons-visibility "popular-samples-prev"
                          "popular-samples-next"
                          @*popular-samples-page
                          @*popular-samples-is-end
                          "samples"))


(defn load-popular-samples []
  (POST "/landing-samples.json"
        {:params        {:offset (* samples-per-landing @*popular-samples-page)}
         :handler       on-popular-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startLanding [_end _page]
  (reset! *popular-samples-is-end _end)
  (reset! *popular-samples-page _page)
  (init-buttons "popular-samples-prev"
                "popular-samples-next"
                *popular-samples-page
                load-popular-samples)
  (set-buttons-visibility "popular-samples-prev"
                          "popular-samples-next"
                          @*popular-samples-page
                          @*popular-samples-is-end
                          "samples"))

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


;;======================================================================================================================
;; Version page
;;======================================================================================================================
(def *version-samples-page (atom 0))
(def *version-samples-is-end (atom false))
(def *version-id (atom nil))

(defn on-version-samples-load [data]
  (dom/removeChildren (dom/getElement "version-samples"))
  (set! (.-innerHTML (.getElementById js/document "version-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *version-samples-is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*version-samples-page)))
  (set-buttons-visibility "version-samples-prev"
                          "version-samples-next"
                          @*version-samples-page
                          @*version-samples-is-end
                          "page"))


(defn load-version-samples []
  (POST "/version-samples.json"
        {:params        {:offset     (* samples-per-page @*version-samples-page)
                         :version_id @*version-id}
         :handler       on-version-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startVersionPage [_end _page _version-id]
  ;(utils/log "Start landing: " _end _page _version-id)
  (reset! *version-samples-is-end _end)
  (reset! *version-samples-page _page)
  (reset! *version-id _version-id)
  (init-buttons "version-samples-prev"
                "version-samples-next"
                *version-samples-page
                load-version-samples)
  (set-buttons-visibility "version-samples-prev"
                          "version-samples-next"
                          @*version-samples-page
                          @*version-samples-is-end
                          "page"))

;;======================================================================================================================
;; Tags page
;;======================================================================================================================
(def *tag-samples-page (atom 0))
(def *tag-samples-is-end (atom false))
(def *tag (atom nil))

(defn on-tag-samples-load [data]
  (dom/removeChildren (dom/getElement "tag-samples"))
  (set! (.-innerHTML (.getElementById js/document "tag-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *tag-samples-is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*tag-samples-page)))
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*tag-samples-page
                          @*tag-samples-is-end
                          "page"))


(defn load-tag-samples [samples-count]
  (POST "/tag-samples.json"
        {:params        {:offset        (* samples-count @*tag-samples-page)
                         :tag           @*tag
                         :samples-count samples-count}
         :handler       on-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startTagPage [_end _page _tag _full-page]
  ;(utils/log "Start tag page: " _end _page _tag _full-page)
  (reset! *tag-samples-is-end _end)
  (reset! *tag-samples-page _page)
  (reset! *tag _tag)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *tag-samples-page
                #(if _full-page
                   (load-tag-samples samples-per-page)
                   (load-tag-samples samples-per-block)))
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*tag-samples-page
                          @*tag-samples-is-end
                          "page"))

;;======================================================================================================================
;; Chart types page
;;======================================================================================================================
(def *chart-types-page (atom 0))
(def *chart-types-is-end (atom false))
(def ^:const chart-types-count 25)

(defn is-end [all-items-count on-page-count page]
  (let [pages (int (.ceil js/Math (/ all-items-count on-page-count)))]
    (>= page (dec pages))))


(defn chart-types-buttons-click []
  (let [box (first (array-seq (.getElementsByClassName js/document "chart-type-container")))
        els (array-seq (.-childNodes box))]
    (dotimes [i (count els)]
      (if (and
            (>= i (* @*chart-types-page 25))
            (< i (+ 25 (* @*chart-types-page 25))))
        (set! (.-display (.-style (nth els i))) "block")
        (set! (.-display (.-style (nth els i))) "none")))
    (reset! *chart-types-is-end (is-end (count els) 25 @*chart-types-page)))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*chart-types-page)))
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*chart-types-page
                          @*chart-types-is-end
                          "page"))


(defn ^:export startChartTypesPage [_end _page]
  (reset! *chart-types-page _page)
  (reset! *chart-types-is-end _end)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *chart-types-page
                chart-types-buttons-click)
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*chart-types-page
                          @*chart-types-is-end
                          "page"))


;;======================================================================================================================
;; Datasets page
;;======================================================================================================================
(def *datasets-page (atom 0))
(def *datasets-is-end (atom false))
(def ^:const datasets-count 6)

(defn datasets-buttons-click []
  (let [box (first (array-seq (.getElementsByClassName js/document "datasets-container")))
        els (array-seq (.-childNodes box))]
    (dotimes [i (count els)]
      (if (and
            (>= i (* @*datasets-page 6))
            (< i (+ 6 (* @*datasets-page 6))))
        (set! (.-display (.-style (nth els i))) "block")
        (set! (.-display (.-style (nth els i))) "none")))
    (reset! *datasets-is-end (is-end (count els) 6 @*datasets-page)))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*datasets-page)))
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*datasets-page
                          @*datasets-is-end
                          "page"))

(defn ^:export startDatasetsPage [_end _page]
  (reset! *datasets-page _page)
  (reset! *datasets-is-end _end)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *datasets-page
                datasets-buttons-click)
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*datasets-page
                          @*datasets-is-end
                          "page"))
