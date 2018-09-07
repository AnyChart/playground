(ns playground.search.views
  (:require [re-frame.core :as rf]
            [goog.dom :as dom]
            [clojure.string :as string]
            [reagent.core :as reagent]))


(defn hint-view [hint-query]
  [:div.search-result
   [:a {:href   (str "/search?q=" hint-query)
        :target "_blank"
        :title  hint-query}
    hint-query]])


(defn search-window []
  (let [hints @(rf/subscribe [:search/query-hints])
        show-hints @(rf/subscribe [:search/show-hints])]
    (when (and show-hints (seq hints))
      [:div#search-results-box.results.hide-outside
       [:div#search-results
        (for [hint hints]
          ^{:key hint}
          [hint-view hint])]])))


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


(defn search-bar-html []
  [:div#search-bar.search-container.hide-outside
   [:div.container-fluid.search.content-container

    [:div.search-area
     [:input#search-input {:placeholder "SEARCH"
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

     [:i#search-input-icon.icon.fas.fa-search {:on-click
                                               #(let [q (.-value (dom/getElement "search-input"))]
                                                  (start-search q))}]

     [:i#search-close-icon.icon.fas.fa-times {:on-click #(rf/dispatch [:search/close])}]]

    [search-window]]])


(defn search-bar []
  (reagent/create-class {:component-did-mount #(.focus (dom/getElement "search-input"))
                         :reagent-render      (fn [] (search-bar-html))}))