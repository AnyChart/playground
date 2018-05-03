(ns playground.site.search.core
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [playground.views.search :as search-view]
            [ajax.core :refer [GET POST]]))


(defn on-load-search-results [data]
  (let [samples (:samples data)]
    (.log js/console data)

    (style/setElementShown (dom/getElement "search-results-box") true)
    (dom/removeChildren (dom/getElement "search-results"))

    (set! (.-innerHTML (.getElementById js/document "search-results"))
          (if (pos? (count samples))
            (apply str (map #(-> % search-view/result-item h/html) samples))
            "Nothing found"))))


(defn make-hint-request [q]
  ;(GET "/search"
  ;      {:params        {:q q}
  ;       :handler       on-load-search-results
  ;       :error-handler #(utils/log "Error!" %)})
  )


(defn make-search-request [q]
  (set! (.-href (.-location js/document)) (str "/search?q=" q)))


(defn hide-all-menus [e]
  (when (not (.-defaultPrevented e))
    (style/setElementShown (dom/getElement "search-results-box") false)))


(defn init []
  (let [input (dom/getElement "search-input")]
    (event/listen input "keydown" (fn [e]
                                    (when (= (.-keyCode e) 13)
                                      (make-search-request (.-value input))))))
  (event/listen js/window "click" hide-all-menus))


(init)


;(defn ^:export setSearchInput
;  ([repo]
;   (set! (.-value (dom/getElement "search-input")) (str "p:" repo " ")))
;  ([repo version]
;   (set! (.-value (dom/getElement "search-input")) (str "p:" repo " " "v:" version " "))))