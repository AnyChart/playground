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


(defn change-title [title]
  (set! (.-title js/document) title))


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
