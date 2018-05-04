(ns playground.search.views
  (:require [re-frame.core :as rf]
            [playground.views.search :as search-views]
            [goog.dom :as dom]
            [clojure.string :as string]))


(defn search-window []
  (let [hints @(rf/subscribe [:search/query-hints])
        show @(rf/subscribe [:search/show])]
    (when (and show (seq hints))
      [:div#search-results-box.results.hide-outside
       [:div#search-results
        (for [hint hints]
          ^{:key hint}
          [search-views/hint hint])]])))


(defn start-search [q]
  (let [q (string/trim q)]
    (when (seq q)
      (.open js/window (str "/search?q=" q) "_blank"))))


(defn search-input []
  [:div.search-box
   [:input#search-input.search {:type        "text"
                                :placeholder "Search"
                                :value       @(rf/subscribe [:search/query])
                                :on-change   #(rf/dispatch [:search/change-query (-> % .-target .-value)])
                                :on-key-down #(let [q (-> % .-target .-value)]
                                                (when (= 13 (.-keyCode %))
                                                  (start-search q)))
                                :on-key-up   #(let [q (string/trim (-> % .-target .-value))]
                                                (if (and (not= 13 (.-keyCode %))
                                                         (pos? (count q)))
                                                  (rf/dispatch [:search/show-hints q])
                                                  (rf/dispatch [:search/hide-hints])))
                                :on-click    #(let [q (string/trim (-> % .-target .-value))]
                                                (.stopPropagation %)
                                                (when (pos? (count q))
                                                  (rf/dispatch [:search/show-hints q])))}]
   [:span#search-input-icon.glyphicon.glyphicon-search {:on-click
                                                        #(let [q (.-value (dom/getElement "search-input"))]
                                                           (start-search q))}]
   [search-window]])