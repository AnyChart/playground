(ns playground.search.views
  (:require [re-frame.core :as rf]
            [playground.views.search :as search-views]
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


(defn search-input []
  [:div.search-box
   [:input#search-input.search {:type        "text"
                                :placeholder "Search"
                                :on-key-down #(let [q (-> % .-target .-value)]
                                                (when (and (= 13 (.-keyCode %))
                                                           (seq q))
                                                  ;(rf/dispatch [:search/search q])
                                                  (.open js/window (str "/search?q=" (string/trim q)) "_blank")))
                                :on-key-up   #(let [q (-> % .-target .-value)]
                                                (if (and (not= 13 (.-keyCode %))
                                                         (> (count q) 1))
                                                  (rf/dispatch [:search/show-hints q])
                                                  (rf/dispatch [:search/hide-hints])))
                                :on-click    #(let [q (-> % .-target .-value)]
                                                (.stopPropagation %)
                                                (when (> (count q) 1)
                                                  (rf/dispatch [:search/show-hints q])))}]
   [:span.glyphicon.glyphicon-search]
   [search-window]])