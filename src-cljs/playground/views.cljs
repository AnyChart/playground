(ns playground.views
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.settings-window.views :as settings-window]
            [playground.embed-window.views :as embed-window]
            [playground.editors.views :as editors]
            [playground.utils :as utils]))

(defn navbar []
  [:nav {:class "navbar navbar-default"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:button {:type          "button"
               :class         "navbar-toggle collapsed"
               :data-toggle   "collapse"
               :data-target   "#navbar"
               :aria-expanded "false"
               :aria-controls "navbar"}
      [:span {:class "sr-only"} "Toggle navigation"]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]]
     [:a {:class "navbar-brand" :href "/"}
      [:img {:src    "/icons/anychart.png"
             :style  {:display "inline-block"}
             :width  "30"
             :height "30"
             :alt    "AnyChart"}]
      "AnyChart Playground"]]
    [:div {:id "navbar" :class "navbar-collapse collapse"}
     [:ul {:class "nav navbar-nav"}
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:run])} "Run"]]
      (when @(rf/subscribe [:show-save-button])
        [:li [:a {:href     "javascript:;"
                  :on-click #(rf/dispatch [:save])} "Save"]])
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:fork])} "Fork"]]
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:settings/show])} "Settings"]]
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:embed/show])} "Embed"]]
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "View"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        [:li [:a {:href @(rf/subscribe [:sample-editor-url])} "Editor"]]
        [:li [:a {:href @(rf/subscribe [:sample-standalone-url])} "Standalone"]]
        [:li [:a {:href @(rf/subscribe [:sample-iframe-url])} "Iframe"]]
        [:li {:role "separator" :class "divider"}]
        [:li [:a {:href     "javascript:;"
                  :on-click #(rf/dispatch [:view/left])} "Left"]]
        [:li [:a {:href     "javascript:;"
                  :on-click #(rf/dispatch [:view/bottom])} "Bottom"]]
        [:li [:a {:href     "javascript:;"
                  :on-click #(rf/dispatch [:view/right])} "Right"]]
        [:li [:a {:href     "javascript:;"
                  :on-click #(rf/dispatch [:view/top])} "Top"]]]]]
     [:ul {:class "nav navbar-nav navbar-right"}
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "Create"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        (for [template @(rf/subscribe [:templates])]
          ^{:key (:name template)} [:li [:a {:href (str "/new?template=" (:url template))} (:name template)]])
        [:li {:role "separator" :class "divider"}]
        [:li [:a {:href "/new"} "From scratch"]]]]
      (if @(rf/subscribe [:can-signin])
        [:li [:a {:href "/signin"} "Log In"]]
        [:li [:a {:href "/signout"} "Log Out"]])
      (when @(rf/subscribe [:can-signup])
        [:li [:a {:href "/signup"} "Sign Up"]])]]]])

(defn footer [])


(defn send-form []
  [:form#run-form
   {:style  {:display "none"}
    :action "/run"
    :target "result-iframe"
    :method "POST"}
   [:input {:name  "code"
            :value @(rf/subscribe [:code])
            :type  "hidden"}]
   [:input {:name  "markup"
            :value @(rf/subscribe [:markup])
            :type  "hidden"}]
   [:input {:name  "style"
            :value @(rf/subscribe [:style])
            :type  "hidden"}]
   [:input {:name  "styles"
            :value @(rf/subscribe [:styles])
            :type  "hidden"}]
   [:input {:name  "scripts"
            :value @(rf/subscribe [:scripts])
            :type  "hidden"}]])


(defn app []
  [:div
   [send-form]
   [navbar]
   [editors/editors]
   [settings-window/settings-window]
   [embed-window/embed-window]])
