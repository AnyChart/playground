(ns playground.site.landing
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.dom.TagName :as TagName]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [playground.views.sample :as sample-view]
            [playground.views.prev-next-buttons :as prev-next-buttons-common]
            [ajax.core :refer [GET POST]]
            [hiccups.runtime :as hiccupsrt]))

;;======================================================================================================================
;; Main consts and funcs
;;======================================================================================================================
(enable-console-print!)

(def ^:const samples-per-page 12)
(def ^:const samples-per-landing 9)
(def ^:const samples-per-block 6)


(defn change-title [title]
  (set! (.-title js/document) title))


(defn str-to-elem [s]
  (let [d (.createElement js/document "div")]
    (set! (.-innerHTML d) s)
    (.-firstChild d)))


;;======================================================================================================================
;; Init buttons
;;======================================================================================================================
(defn add-prev-event [prev-btn-cls *page load-fn *loading]
  (.forEach (dom/getElementsByClass prev-btn-cls)
            #(event/listen % "click" (fn [e]
                                        (.preventDefault e)
                                        (when-not @*loading
                                          (reset! *loading true)
                                          (swap! *page dec)
                                          (load-fn))))))


(defn add-next-event [next-btn-cls *page load-fn *loading]
  (.forEach (dom/getElementsByClass next-btn-cls)
            #(event/listen % "click" (fn [e]
                                        (.preventDefault e)
                                        (when-not @*loading
                                          (reset! *loading true)
                                          (swap! *page inc)
                                          (load-fn))))))


(defn init-buttons [prev-btn-cls next-btn-cls *page load-fn *loading]
  (add-prev-event prev-btn-cls *page load-fn *loading)
  (add-next-event next-btn-cls *page load-fn *loading))


;;======================================================================================================================
;; Update buttons
;;======================================================================================================================
(defn set-prev-button [cls *page url]
  (.forEach (dom/getElementsByClass cls)
            (fn [elem]
              (if (pos? @*page)
               (do
                 (style/setStyle elem "visibility" "visible")
                 (.setAttribute elem "href" (str url @*page))
                 (.setAttribute elem "title" (str "Prev page, " @*page)))
               (do
                 (style/setStyle elem "visibility" "hidden")
                 (.setAttribute elem "href" "#")
                 (.setAttribute elem "title" ""))))))


(defn set-next-button [cls *page end url]
  (.forEach (dom/getElementsByClass cls)
            (fn [elem]
              (if-not end
               (do
                 (style/setStyle elem "visibility" "visible")
                 (.setAttribute elem "href" (str url (inc (inc @*page))))
                 (.setAttribute elem "title" (str "Next page, " (inc (inc @*page)))))
               (do
                 (style/setStyle elem "visibility" "hidden")
                 (.setAttribute elem "href" "#")
                 (.setAttribute elem "title" ""))))))


(defn update-buttons [prev-btn-cls
                      next-btn-cls
                      *page
                      end
                      url
                      load-fn
                      *loading]
  (set-prev-button prev-btn-cls *page url)
  (set-next-button next-btn-cls *page end url)
  (reset! *loading false))


(defn update-pagination [*page *max-page *loading url load-fn]
  (when (pos? @*max-page)
    (let [markup (h/html (prev-next-buttons-common/pagination-markup @*page @*max-page url))
          pagination-boxes (dom/getElementsByClass "pagination-box")]
      (.forEach pagination-boxes
        (fn [pagination-box]
          (dom/removeChildren pagination-box)
          (set! (.-innerHTML pagination-box) markup)
          (let [buttons (dom/getElementsByTagName TagName/LI pagination-box)]
            (dotimes [i (.-length buttons)]
              (let [button (aget buttons i)]
                (event/listen button "click" #(let [num (.-textContent (.-target %))
                                                    num (js/parseInt num)]
                                                (.preventDefault %)
                                                ;(println "click" num)
                                                (when (pos? num)
                                                  (when-not @*loading
                                                    (reset! *loading true)
                                                    (reset! *page (dec num))
                                                    (load-fn)))))))))))))