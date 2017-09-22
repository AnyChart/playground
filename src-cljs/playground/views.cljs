(ns playground.views
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.settings-window.views :as settings-window]
            [playground.embed-window.views :as embed-window]
            [playground.editors.views :as editors]
            [playground.tips.views :as tips]
            [playground.utils :as utils]
            [playground.utils.utils :as utils-main]))

(defn navbar []
  [:header
   [:div.header-box

    [:div.logo
     [:div.logo-img
      [:img {:src "/icons/editor/logo.svg"}]]
     [:a.logo-label {:href "/"}
      "AnyChart " [:b "Playground"]]]

    [:ul.nav.navbar-nav.left-navbar

     [:li [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:run])}
           [:div.icon.icon-run]
           [:span "Run"]]]

     (when @(rf/subscribe [:show-save-button])
       [:li [:a {:href     "javascript:;"
                 :on-click #(rf/dispatch [:save])}
             [:div.icon.icon-save]
             [:span "Save"]]])

     [:li [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:fork])}
           [:div.icon.icon-fork]
           [:span "Fork"]]]

     [:li [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:settings/show])}
           [:div.icon.icon-settings]
           [:span "Settings"]]]

     [:li [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:embed/show])}
           [:div.icon.icon-embed]
           [:span "Embed"]]]

     [:li.dropdown
      [:a.dropdown-toggle {:href          "#"
                           :data-toggle   "dropdown"
                           :role          "button"
                           :aria-haspopup "true"
                           :aria-expanded "false"}
       [:div.icon.icon-view]
       [:span "View"]
       [:span.caret]]
      [:ul.dropdown-menu
       [:li [:a {:href @(rf/subscribe [:sample-editor-url])}
             [:img.icon {:src "/icons/editor/editor.svg"}]
             [:span "Editor"]]]
       [:li [:a {:href     "javascript:;"                   ;@(rf/subscribe [:sample-standalone-url])
                 :on-click #(rf/dispatch [:view/standalone])}
             [:img.icon {:src "/icons/editor/standalone.svg"}]
             "Standalone"]]
       [:li [:a {:href @(rf/subscribe [:sample-iframe-url])}
             [:img.icon {:src "/icons/editor/iframe.svg"}]
             "Iframe"]]
       [:li.divider {:role "separator"}]
       [:li [:a {:href     "javascript:;"
                 :on-click #(rf/dispatch [:view/left])}
             [:img.icon {:src "/icons/editor/left.svg"}]
             "Left"]]
       [:li [:a {:href     "javascript:;"
                 :on-click #(rf/dispatch [:view/bottom])}
             [:img.icon {:src "/icons/editor/bottom.svg"}]
             "Bottom"]]
       [:li [:a {:href     "javascript:;"
                 :on-click #(rf/dispatch [:view/right])}
             [:img.icon {:src "/icons/editor/right.svg"}]
             "Right"]]
       [:li [:a {:href     "javascript:;"
                 :on-click #(rf/dispatch [:view/top])}
             [:img.icon {:src "/icons/editor/top.svg"}]
             "Top"]]]]]
    [:ul.nav.navbar-nav.navbar-right

     [:li.dropdown
      [:a.dropdown-toggle {:href          "#"
                           :data-toggle   "dropdown"
                           :role          "button"
                           :aria-haspopup "true"
                           :aria-expanded "false"} "Create"
       [:span.caret]]
      [:ul.dropdown-menu
       (for [template @(rf/subscribe [:templates])]
         ^{:key (:name template)}
         [:li
          [:a {:href  (str "/new?template=" (:url template))
               :title (str "Create " (:name template))}
           [:img {:src (str "/icons/" (utils-main/name->url (:name template)) ".svg")
                  :alt (str "Create " (:name template) " button icon")}]
           (:name template)]])
       [:li.divider {:role "separator"}]
       [:li
        [:a {:href  "/new"
             :title "Create from scratch"}
         [:img {:src (str "/icons/from-scratch.svg")
                :alt "Create from scratch button icon"}]
         "From scratch"]]]
      ]

     ;(if @(rf/subscribe [:can-signin])
     ;  [:li [:a {:href "/signin"} "Log In"]]
     ;  [:li [:a {:href "/signout"} "Log Out"]])
     ;(when @(rf/subscribe [:can-signup])
     ;  [:li [:a {:href "/signup"} "Sign Up"]])
     ]]])

(defn footer [])


(defn send-form []
  [:form#run-form
   {:style  {:display "none"}
    :action "/run"
    :target "result-iframe"
    :method "POST"}
   [:input {:name  "code"
            :value @(rf/subscribe [:sample/code])
            :type  "hidden"}]
   [:input {:name  "markup"
            :value @(rf/subscribe [:sample/markup])
            :type  "hidden"}]
   [:input {:name  "style"
            :value @(rf/subscribe [:sample/style])
            :type  "hidden"}]
   [:input {:name  "styles"
            :value @(rf/subscribe [:sample/styles])
            :type  "hidden"}]
   [:input {:name  "scripts"
            :value @(rf/subscribe [:sample/scripts])
            :type  "hidden"}]])


(defn app []
  [:div
   [send-form]
   [navbar]
   [editors/editors]
   [tips/tips]
   [settings-window/settings-window]
   [embed-window/embed-window]])
