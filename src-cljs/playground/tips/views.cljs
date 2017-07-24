(ns playground.tips.views
  (:require [re-frame.core :as rf]))


(defn tip [tip]
  (let [id (str "checkbox" (rand-int (.-MAX_SAFE_INTEGER js/Number)))]
    [:div.tip
     [:h3 (:title tip)]
     [:p (:description tip)]
     [:p "Example"]
     [:div [:pre (:example tip)]]

     [:div {:style {:display         "flex"
                    :justify-content "space-between"}}
      [:div.checkbox
       [:label {:for   id
                :style {:padding-left 0}}
        [:span "Never show this tip again"]]
       [:input {:id        id
                :type      "checkbox"
                :style     {:margin-left "5px"}
                :on-change #(rf/dispatch [:tips/never-show-again-change tip (-> % .-target .-checked)])}]]
      [:button.btn.btn-link {:type     "button"
                             :on-click #(rf/dispatch [:tips.tip/close (:link tip)])} "Hide"]]

     [:span.glyphicon.glyphicon-remove.tip-close {:aria-hidden true
                                                  :on-click    #(rf/dispatch [:tips.tip/close (:link tip)])}]]))


(defn tips []
  [:div.tips
   (for [tip-data @(rf/subscribe [:tips/current])]
     ^{:key (:link tip-data)} [tip tip-data])])
