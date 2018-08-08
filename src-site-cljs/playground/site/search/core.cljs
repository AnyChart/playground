(ns playground.site.search.core
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))


(def search-bar-id "search-bar")
(def search-bar-open-icon-id "search-bar-open-icon")

(def search-input-id "search-input")
(def search-input-icon-id "search-input-icon")
(def search-close-icon-id "search-close-icon")

(def search-results-id "search-results")
(def search-results-box-id "search-results-box")


(def *hints (atom []))


(defn hint-view [hint-query]
  [:div.search-result
   [:a {:href  (str "/search?q=" hint-query)
        :title hint-query}
    hint-query]])


(defn hide-hints []
  (style/setElementShown (dom/getElement search-results-box-id) false))


(defn show-hints [q]
  (let [hints (take 30 (filter (fn [hint]
                                 (string/includes? (string/lower-case hint)
                                                   (string/lower-case (string/trim q))))
                               @*hints))]
    (if (seq hints)
      (do
        (style/setElementShown (dom/getElement search-results-box-id) true)
        (dom/removeChildren (dom/getElement search-results-id))
        (set! (.-innerHTML (.getElementById js/document search-results-id))
              (if (pos? (count hints))
                (apply str (map #(-> % hint-view h/html) hints))
                "Nothing found")))
      (hide-hints))))


(defn show-search-bar []
  (style/setElementShown (dom/getElement search-bar-id) true))


(defn hide-search-bar []
  (style/setElementShown (dom/getElement search-bar-id) false))


(defn make-search-request [q]
  (let [q (string/trim q)]
    (when (seq q)
      (set! (.-href (.-location js/document)) (str "/search?q=" q)))))


(defn init []
  (let [input (dom/getElement search-input-id)]
    (event/listen input "keydown" (fn [e]
                                    (let [q (string/trim (-> e .-target .-value))]
                                      (when (and (= (.-keyCode e) 13)
                                                 (seq q))
                                        (make-search-request q)))))
    (event/listen input "keyup" (fn [e]
                                  (let [q (string/trim (-> e .-target .-value))]
                                    (if (and (not= (.-keyCode e) 13)
                                             (pos? (count q)))
                                      (show-hints q)
                                      (hide-hints)))))
    (event/listen input "click" (fn [e]
                                  (let [q (string/trim (-> e .-target .-value))]
                                    (.stopPropagation e)
                                    (when (pos? (count q))
                                      (show-hints q)))))

    (event/listen (dom/getElement search-input-icon-id) "click" (fn [e]
                                                                  (let [q (string/trim (.-value input))]
                                                                    (when (seq q)
                                                                      (make-search-request q))))))

  (event/listen (dom/getElement search-bar-open-icon-id) "click" (fn [e]
                                                                   (.stopPropagation e)
                                                                   (show-search-bar)
                                                                   (.focus (dom/getElement search-input-id))
                                                                   (style/setStyle (dom/getElement "navbar-container")
                                                                                   "box-shadow"
                                                                                   "0 40px 140px #113b6b")))

  (event/listen (dom/getElement search-close-icon-id) "click" (fn [e]
                                                                (.stopPropagation e)
                                                                (hide-search-bar)
                                                                (style/setStyle (dom/getElement "navbar-container")
                                                                                "box-shadow"
                                                                                "none")))

  (GET "/search-hints"
       {:handler       #(let [hints (sort (map :name %))]
                          (reset! *hints hints))
        :error-handler #(utils/log "Error!" %)})

  (event/listen js/window "click" (fn [e]
                                    (when (not (.-defaultPrevented e))
                                      (hide-hints)))))


(init)