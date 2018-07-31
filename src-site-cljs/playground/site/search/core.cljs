(ns playground.site.search.core
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))


(def *hints (atom []))


(defn hint-view [hint-query]
  [:div.search-result
   [:a {:href  (str "/search?q=" hint-query)
        :title hint-query}
    hint-query]])


(defn hide-hints []
  (style/setElementShown (dom/getElement "search-results-box") false))


(defn show-hints [q]
  (let [hints (take 30 (filter (fn [hint]
                                 (string/includes? (string/lower-case hint)
                                                   (string/lower-case (string/trim q))))
                               @*hints))]
    (if (seq hints)
      (do
        (style/setElementShown (dom/getElement "search-results-box") true)
        (dom/removeChildren (dom/getElement "search-results"))
        (set! (.-innerHTML (.getElementById js/document "search-results"))
              (if (pos? (count hints))
                (apply str (map #(-> % hint-view h/html) hints))
                "Nothing found")))
      (hide-hints))))


(defn make-search-request [q]
  (let [q (string/trim q)]
    (when (seq q)
      (set! (.-href (.-location js/document)) (str "/search?q=" q)))))


(defn init []
  (let [input (dom/getElement "search-input")]
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
    (event/listen (dom/getElement "search-input-icon") "click" (fn [e]
                                                                 (let [q (string/trim (.-value input))]
                                                                   (when (seq q)
                                                                     (make-search-request q))))))
  (GET "/search-hints"
       {:handler       #(let [hints (sort (map :name %))]
                          (reset! *hints hints))
        :error-handler #(utils/log "Error!" %)})

  (event/listen js/window "click" (fn [e]
                                    (when (not (.-defaultPrevented e))
                                      (hide-hints)))))


;(init)