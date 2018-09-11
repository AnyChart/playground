(ns playground.settings-window.views
  (:require [re-frame.core :as rf]
            [playground.settings-window.javascript-tab.views :as js-tab-view]
            [playground.settings-window.css-tab.views :as css-tab-view]
            [playground.settings-window.datasets-tab.views :as datasets-tab-view]
            [playground.settings-window.general-tab.views :as general-tab-view]
            [playground.data.consts :as consts]))


(defn nav-menu []
  [:ul.nav.nav-tabs.settings-tabs
   [:li {:class (when @(rf/subscribe [:settings/general-tab?]) "active")}
    [:a {:href     "javascript:;"
         :role     "button"
         :on-click #(rf/dispatch [:settings/general-tab])} "General"]]

   (let [correct @(rf/subscribe [:settings.javascript-tab/correct-tab])]
     [:li {:class (when @(rf/subscribe [:settings/javascript-tab?]) "active")}
      [:a {:href     "javascript:;"
           :role     "button"
           :title    (when-not correct consts/settings-warning)
           :on-click #(rf/dispatch [:settings/javascript-tab])}
       "JavaScript"
       (when-not correct
         ;[:span.glyphicon.glyphicon-warning-sign]
         [:i.fas.fa-exclamation-triangle.icon-warning])]])

   (let [correct @(rf/subscribe [:settings.css-tab/correct-tab])]
     [:li {:class (when @(rf/subscribe [:settings/css-tab?]) "active")}
      [:a {:href     "javascript:;"
           :role     "button"
           :title    (when-not correct consts/settings-warning)
           :on-click #(rf/dispatch [:settings/css-tab])}
       "CSS"
       (when-not correct
         ;[:span.glyphicon.glyphicon-warning-sign]
         [:i.fas.fa-exclamation-triangle.icon-warning])]])

   ;; TODO: wait datasests texts
   ;[:li {:class (when @(rf/subscribe [:settings/datasets-tab?]) "active")}
   ; [:a {:href     "javascript:;"
   ;      :role     "button"
   ;      :on-click #(rf/dispatch [:settings/datasets-tab])} "Data Sets"]]
   ])


(defn settings-window []
  (when @(rf/subscribe [:settings/show])
    [:div.dropdown-window.settings-window.hide-outside
     [nav-menu]

     (when @(rf/subscribe [:settings/general-tab?])
       [general-tab-view/general-tab])

     (when @(rf/subscribe [:settings/javascript-tab?])
       [js-tab-view/javascript-tab])

     (when @(rf/subscribe [:settings/css-tab?])
       [css-tab-view/css-tab])

     (when @(rf/subscribe [:settings/datasets-tab?])
       [datasets-tab-view/datasets-tab])]))
