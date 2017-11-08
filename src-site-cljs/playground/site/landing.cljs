(ns playground.site.landing
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [playground.views.sample :as sample-view]
            [ajax.core :refer [GET POST]]
            [hiccups.runtime :as hiccupsrt]
            [clojure.string :as s]
            [playground.views.prev-next-buttons :as prev-next-buttons-common]))

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


(defn add-prev-event [prev-btn-id *page load-fn]
  (when-let [btn (dom/getElement prev-btn-id)]
    (event/listen btn "click" (fn [e]
                                (.preventDefault e)
                                (swap! *page dec)
                                (load-fn)))))


(defn add-next-event [next-btn-id *page load-fn]
  (when-let [btn (dom/getElement next-btn-id)]
    (event/listen btn "click" (fn [e]
                                (.preventDefault e)
                                (swap! *page inc)
                                (load-fn)))))

(defn init-buttons [prev-btn-id next-btn-id *page load-fn]
  (add-prev-event prev-btn-id *page load-fn)
  (add-next-event next-btn-id *page load-fn))


(defn add-or-remove-prev-button-if-need [id *page url load-fn]
  (if (pos? @*page)
    (when-not (.getElementById js/document id)
      (let [container (.getElementById js/document "prev-next-buttons")]
        (.prepend container (str-to-elem (h/html (prev-next-buttons-common/prev-button id @*page url))))
        (add-prev-event id *page load-fn)))
    (if-let [btn (.getElementById js/document id)]
      (.remove btn))))


(defn add-or-remove-next-button-if-need [id *page end url load-fn]
  (if-not end
    (when-not (.getElementById js/document id)
      (let [container (.getElementById js/document "prev-next-buttons")]
        (.append container (str-to-elem (h/html (prev-next-buttons-common/next-button id @*page url))))
        (add-next-event id *page load-fn)))
    (if-let [btn (.getElementById js/document id)]
      (.remove btn))))


(defn update-buttons [prev-btn-id
                      next-btn-id
                      *page
                      end
                      url
                      load-fn]
  (add-or-remove-prev-button-if-need prev-btn-id *page url load-fn)
  (add-or-remove-next-button-if-need next-btn-id *page end url load-fn)
  (let [prev-button (dom/getElement prev-btn-id)
        next-button (dom/getElement next-btn-id)]
    (when prev-button
      (.setAttribute prev-button "href" (str url @*page))
      (.setAttribute prev-button "title" (str "Prev page, " @*page)))
    (when next-button
      (.setAttribute next-button "href" (str url (inc (inc @*page))))
      (.setAttribute next-button "title" (str "Next page, " (inc (inc @*page)))))))
