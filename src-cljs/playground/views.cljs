(ns playground.views
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]))


;; -- View Functions ----------------------------------------------
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
     [:a {:class "navbar-brand" :href "//anychart.com/"}
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
      [:li [:a {:href "#"} "Save"]]
      [:li [:a {:href "#"} "Fork"]]
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "View"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        [:li [:a {:href "#"} "Editor"]]
        [:li [:a {:href "#"} "Standalone"]]
        [:li [:a {:href "#"} "Iframe"]]]]]
     [:ul {:class "nav navbar-nav navbar-right"}
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "New Chart"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        [:li [:a {:href "#"} "Chart"]]
        [:li [:a {:href "#"} "Stock"]]
        [:li [:a {:href "#"} "Gantt"]]
        [:li [:a {:href "#"} "Map"]]
        [:li {:role "separator" :class "divider"}]
        [:li [:a {:href "#"} "From scratch"]]]]
      [:li [:a {:href "/signin"} "Log In"]]
      [:li [:a {:href "/signup"} "Sign Up"]]]]]])

(defn footer []
  )

(defn editors []
  [:div.column-container {:style {:height @(rf/subscribe [:editors-height])}}
   [:fieldset.column-left
    [:div.editor-box
     [:div#markup-editor]]
    [:div.editor-box
     [:div#style-editor]]
    [:div.editor-box
     [:div#code-editor]]]
   [:fieldset.column-right
    [:div.result
     [:iframe#result-iframe {:name              "result-iframe"
                             :class             "iframe-result"
                             :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                             :allowTransparency "true"
                             :allowFullScreen   "true"
                             :src               @(rf/subscribe [:sample-iframe-url])}]]]])

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
            :type  "hidden"}]])

(defn app []
  [:div
   [send-form]
   [navbar]
   [editors]])
