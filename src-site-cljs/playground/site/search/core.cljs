(ns playground.site.search.core
  (:require-macros [hiccups.core :as h])
  (:require [goog.dom :as dom]
            [goog.events :as event]
            [goog.style :as style]
            [playground.site.utils :as utils]
            [playground.views.search :as search-view]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))


(def *hints (atom []))


;(defn on-load-search-results [data]
;  (let [samples (:samples data)]
;    (.log js/console data)
;
;    (style/setElementShown (dom/getElement "search-results-box") true)
;    (dom/removeChildren (dom/getElement "search-results"))
;
;    (set! (.-innerHTML (.getElementById js/document "search-results"))
;          (if (pos? (count samples))
;            (apply str (map #(-> % search-view/result-item h/html) samples))
;            "Nothing found"))))


;(defn make-hint-request [q]
;  (GET "/search"
;        {:params        {:q q}
;         :handler       on-load-search-results
;         :error-handler #(utils/log "Error!" %)}))


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
                (apply str (map #(-> % search-view/hint h/html) hints))
                "Nothing found")))
      (hide-hints))))


(defn make-search-request [q]
  (set! (.-href (.-location js/document)) (str "/search?q=" q)))


(defn init []
  (let [input (dom/getElement "search-input")]
    (event/listen input "keydown" (fn [e]
                                    (let [q (-> e .-target .-value)]
                                      (when (and (= (.-keyCode e) 13)
                                                 (seq q))
                                        (make-search-request q)))))
    (event/listen input "keyup" (fn [e]
                                  (let [q (-> e .-target .-value)]
                                    (if (and (not= (.-keyCode e) 13)
                                             (> (count q) 1))
                                      (show-hints q)
                                      (hide-hints)))))
    (event/listen input "click" (fn [e]
                                  (let [q (-> e .-target .-value)]
                                    (.stopPropagation e)
                                    (when (> (count q) 1)
                                      (show-hints q))))))
  (GET "/search-hints"
       {:handler       #(let [hints (sort (map :name %))]
                          (reset! *hints hints))
        :error-handler #(utils/log "Error!" %)})
  (event/listen js/window "click" (fn [e]
                                    (when (not (.-defaultPrevented e))
                                      (hide-hints)))))


(init)


;(defn ^:export setSearchInput
;  ([repo]
;   (set! (.-value (dom/getElement "search-input")) (str "p:" repo " ")))
;  ([repo version]
;   (set! (.-value (dom/getElement "search-input")) (str "p:" repo " " "v:" version " "))))