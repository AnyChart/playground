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

(def ^:const samples-per-page 12)

(def offset (atom 0))
(def page (atom 0))
(def end (atom false))
;; if version-id is nil then it's landing, not version-page
(def version-id (atom nil))

(defn set-buttons-visibility []
  ;(utils/log "set visibitlity: page, end: " @page @end)
  (let [prevButton (dom/getElement "prevButton")
        nextButton (dom/getElement "nextButton")]
    (style/setElementShown prevButton (pos? @page))
    (.setAttribute prevButton "href" (str "/?page=" @page))
    (.setAttribute prevButton "title" (str "Prev page, " @page))
    (style/setElementShown nextButton (not @end))
    (.setAttribute nextButton "href" (str "/?page=" (inc (inc @page))))
    (.setAttribute nextButton "title" (str "Next page, " (inc (inc @page))))))

(defn on-samples-load [data]
  ;(utils/log data)
  (dom/removeChildren (dom/getElement "samples-container"))
  (set! (.-innerHTML (.getElementById js/document "samples-container"))
        (apply str (map #(-> % sample-view/sample-landing h/html) (:samples data))))
  (reset! end (:end data))
  (.pushState (.-history js/window) nil nil (str
                                              (when (not= (.-pathname (.-location js/window)) "/")
                                                (.-pathname (.-location js/window)))
                                              "?page=" (inc @page)))
  (set-buttons-visibility))

(defn load-samples []
  (if @version-id
    (POST "/version-samples.json"
          {:params        {:offset     (* samples-per-page @page)
                           :version_id @version-id}
           :handler       on-samples-load
           :error-handler #(utils/log "Error!" %)})
    (POST "/landing-samples.json"
          {:params        {:offset (* samples-per-page @page)}
           :handler       on-samples-load
           :error-handler #(utils/log "Error!" %)})))

(defn init-buttons []
  (let [prevButton (dom/getElement "prevButton")
        nextButton (dom/getElement "nextButton")]
    (event/listen prevButton "click" (fn [e]
                                       (.preventDefault e)
                                       (swap! page dec)
                                       (load-samples)))
    (event/listen nextButton "click" (fn [e]
                                       (.preventDefault e)
                                       (swap! page inc)
                                       (load-samples)))))

(defn ^:export start [end-val page-val & [version-id-val]]
  ;(utils/log "Start site: " end-val version-id-val)
  (reset! end end-val)
  (reset! page page-val)
  (when version-id (reset! version-id version-id-val))
  (init-buttons)
  (set-buttons-visibility))

