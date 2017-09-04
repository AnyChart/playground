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
(def ^:const samples-per-block 6)


(defn set-buttons-visibility [prev-btn-name
                              next-btn-name
                              page
                              end
                              url-param]
  (utils/log "set visibitlity: " prev-btn-name next-btn-name page end url-param)
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
                    load-samples]
  (let [prev-button (dom/getElement prev-btn-name)
        next-button (dom/getElement next-btn-name)]
    (event/listen prev-button "click" (fn [e]
                                        (.preventDefault e)
                                        (swap! *page dec)
                                        (load-samples)))
    (event/listen next-button "click" (fn [e]
                                        (.preventDefault e)
                                        (swap! *page inc)
                                        (load-samples)))))

;;======================================================================================================================
;; Landing page
;;======================================================================================================================
(def *popular-samples-page (atom 0))
(def *popular-samples-is-end (atom false))


(defn on-popular-samples-load [data]
  (dom/removeChildren (dom/getElement "popular-samples"))
  (set! (.-innerHTML (.getElementById js/document "popular-samples"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *popular-samples-is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?samples=" (inc @*popular-samples-page)
                                                 "&tags=" (inc @*landing-tag-samples-page)))
  (set-buttons-visibility "popular-samples-prev"
                          "popular-samples-next"
                          @*popular-samples-page
                          @*popular-samples-is-end
                          "samples"))


(defn load-popular-samples []
  (POST "/landing-samples.json"
        {:params        {:offset (* samples-per-block @*popular-samples-page)}
         :handler       on-popular-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startLanding [_end _page]
  (utils/log "Start landing: " _end _page)
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
(def *landing-tag-samples-page (atom 0))
(def *landing-tag-samples-is-end (atom false))


(defn on-landing-tag-samples-load [data]
  (dom/removeChildren (dom/getElement "popular-tags"))
  (set! (.-innerHTML (.getElementById js/document "popular-tags"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! *popular-samples-is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?samples=" (inc @*popular-samples-page)
                                                 "&tags=" (inc @*landing-tag-samples-page)))
  (set-buttons-visibility "popular-tags-prev"
                          "popular-tags-next"
                          @*landing-tag-samples-page
                          @*landing-tag-samples-is-end
                          "tags"))


(defn load-landing-tag-samples []
  (POST "/landing-tag-samples.json"
        {:params        {:offset (* samples-per-block @*landing-tag-samples-page)}
         :handler       on-landing-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startLandingTag [_end _page]
  (utils/log "Start landing: " _end _page)
  (reset! *landing-tag-samples-is-end _end)
  (reset! *landing-tag-samples-page _page)
  (init-buttons "popular-tags-prev"
                "popular-tags-next"
                *landing-tag-samples-page
                load-landing-tag-samples)
  (set-buttons-visibility "popular-tags-prev"
                          "popular-tags-next"
                          @*landing-tag-samples-page
                          @*landing-tag-samples-is-end
                          "tags"))


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
  (utils/log "Start landing: " _end _page _version-id)
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


(defn load-tag-samples []
  (POST "/tag-samples.json"
        {:params        {:offset (* samples-per-page @*tag-samples-page)
                         :tag    @*tag}
         :handler       on-tag-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startTagPage [_end _page _tag]
  (utils/log "Start tag page: " _end _page _tag)
  (reset! *tag-samples-is-end _end)
  (reset! *tag-samples-page _page)
  (reset! *tag _tag)
  (init-buttons "tag-samples-prev"
                "tag-samples-next"
                *tag-samples-page
                load-tag-samples)
  (set-buttons-visibility "tag-samples-prev"
                          "tag-samples-next"
                          @*tag-samples-page
                          @*tag-samples-is-end
                          "page"))


;(def page (atom 0))
;(def end (atom false))
;
;;; when some? version-id, it's version-page
;(def version-id (atom nil))
;;; when some? tag it's tag page
;(def tag (atom nil))
;;; else it's landing page


;(defn set-buttons-visibility []
;  ;(utils/log "set visibitlity: page, end: " @page @end)
;  (let [prevButton (dom/getElement "prevButton")
;        nextButton (dom/getElement "nextButton")]
;    (style/setElementShown prevButton (pos? @page))
;    (.setAttribute prevButton "href" (str "/?page=" @page))
;    (.setAttribute prevButton "title" (str "Prev page, " @page))
;    (style/setElementShown nextButton (not @end))
;    (.setAttribute nextButton "href" (str "/?page=" (inc (inc @page))))
;    (.setAttribute nextButton "title" (str "Next page, " (inc (inc @page))))))

;(defn on-samples-load [data]
;  (dom/removeChildren (dom/getElement "samples-container"))
;  (set! (.-innerHTML (.getElementById js/document "samples-container"))
;        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
;  (reset! end (:end data))
;  (.pushState (.-history js/window) nil nil (str
;                                              (when (not= (.-pathname (.-location js/window)) "/")
;                                                (.-pathname (.-location js/window)))
;                                              "?page=" (inc @page)))
;  (set-buttons-visibility))
;
;(defn load-samples []
;  (if @version-id
;    (POST "/version-samples.json"
;          {:params        {:offset     (* samples-per-page @page)
;                           :version_id @version-id}
;           :handler       on-samples-load
;           :error-handler #(utils/log "Error!" %)})
;    (if @tag
;      (POST "/tag-samples.json"
;            {:params        {:offset (* samples-per-page @page)
;                             :tag    @tag}
;             :handler       on-samples-load
;             :error-handler #(utils/log "Error!" %)})
;      (POST "/landing-samples.json"
;            {:params        {:offset (* samples-per-page @page)}
;             :handler       on-samples-load
;             :error-handler #(utils/log "Error!" %)}))))

;(defn init-buttons []
;  (let [prevButton (dom/getElement "prevButton")
;        nextButton (dom/getElement "nextButton")]
;    (event/listen prevButton "click" (fn [e]
;                                       (.preventDefault e)
;                                       (swap! page dec)
;                                       (load-samples)))
;    (event/listen nextButton "click" (fn [e]
;                                       (.preventDefault e)
;                                       (swap! page inc)
;                                       (load-samples)))))

;(defn ^:export start [end-val page-val & [version-id-val tag-val]]
;  (utils/log "Start site: " end-val version-id-val tag-val)
;  (reset! end end-val)
;  (reset! page page-val)
;  (when version-id-val (reset! version-id version-id-val))
;  (when tag-val (reset! tag tag-val))
;  (init-buttons)
;  (set-buttons-visibility))

