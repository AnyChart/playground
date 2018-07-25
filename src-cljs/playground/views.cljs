(ns playground.views
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.settings-window.views :as settings-window]
            [playground.export-window.views :as export-window]
            [playground.editors.views :as editors]
            [playground.tips.views :as tips]
            [playground.sidemenu.views :as sidemenu]
            [playground.search.views :as search]
            [playground.utils.utils :as utils-main]
            [playground.data.consts :as consts]
            [playground.modal-window.views :as modal-view]))


(defn navbar []
  [:header
   [:div.header-box

    [:div.logo
     [:div.logo-img {:on-click #(rf/dispatch [:left-menu/toggle])}
      [:div.border-icon]
      [:div.chart-row
       [:span.chart-col.green]
       [:span.chart-col.orange]
       [:span.chart-col.red]]]
     [:div.brand-label [:a.logo-label {:href "/"} "AnyChart " [:b "Playground"]]]]

    [:div.top-menu
     [:ul.nav.navbar-nav.left-navbar

      [:li [:button.btn.btn-link {:on-click #(do (rf/dispatch [:click-run]))}
            [:div.icon.icon-run]
            [:span "Run"]]]

      (when @(rf/subscribe [:show-save-button])
        [:li [:button.btn.btn-link {:on-click #(do
                                                 (rf/dispatch [:settings/refresh-tags])
                                                 (rf/dispatch [:save]))}
              [:div.icon.icon-save]
              [:span "Save"]]])

      [:li [:button.btn.btn-link {:on-click #(do
                                               (rf/dispatch [:settings/refresh-tags])
                                               (rf/dispatch [:fork]))}
            [:div.icon.icon-fork]
            [:span "Fork"]]]

      (let [show-warning (not @(rf/subscribe [:settings/correct-scripts-styles]))]
        [:li.dropdown {:title (when show-warning consts/settings-warning)}
         [:button.btn.btn-link {:on-click #(do
                                             (rf/dispatch [:settings/refresh-tags])
                                             (rf/dispatch [:settings/update-datasets])
                                             (rf/dispatch [:settings/show]))
                                :class    (when @(rf/subscribe [:settings/show]) "active")}
          [:div.icon.icon-settings]
          [:span "Settings"]
          [:span.caret]
          (when show-warning
            [:span.glyphicon.glyphicon-warning-sign])
          ]])

      [:li.dropdown
       [:button.btn.btn-link {:on-click #(rf/dispatch [:embed/show])
                              :class    (when @(rf/subscribe [:embed/show]) "active")}
        [:div.icon.icon-embed]
        [:span "Export"]
        [:span.caret]]]

      [:li.dropdown
       [:button.btn.btn-link.dropdown-toggle {:href          "#"
                                              ;:data-toggle   "dropdown"
                                              :role          "button"
                                              :aria-haspopup "true"
                                              :aria-expanded "false"
                                              :on-click      #(rf/dispatch [:view-menu/show])}
        [:div.icon.icon-view]
        [:span "View"]
        [:span.caret]]
       [:ul.dropdown-menu
        {:style {:display (if @(rf/subscribe [:view-menu/show]) "block" "none")}}
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/editor])}
              [:img.icon {:src "/icons/editor/editor.svg"}]
              "Editor"]]
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/standalone])}
              [:img.icon {:src "/icons/editor/standalone.svg"}]
              "View only"]]
        [:li [:a {:href @(rf/subscribe [:sample-iframe-url])}
              [:img.icon {:src "/icons/editor/iframe.svg"}]
              "Iframe"]]
        [:li.divider {:role "separator"}]
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/left])}
              [:img.icon {:src "/icons/editor/left.svg"}]
              "Left"]]
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/bottom])}
              [:img.icon {:src "/icons/editor/bottom.svg"}]
              "Bottom"]]
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/right])}
              [:img.icon {:src "/icons/editor/right.svg"}]
              "Right"]]
        [:li [:button.btn.btn-link {:on-click #(rf/dispatch [:view/top])}
              [:img.icon {:src "/icons/editor/top.svg"}]
              "Top"]]]]

      [:li.dropdown
       [:button.btn.btn-link.dropdown-toggle {:href          "#"
                                              :role          "button"
                                              :aria-haspopup "true"
                                              :aria-expanded "false"
                                              :on-click      #(rf/dispatch [:download-menu/show])}
        [:div.icon.icon-download]
        [:span "Download"]
        [:span.caret]]
       [:ul.dropdown-menu
        {:style {:display (if @(rf/subscribe [:download-menu/show]) "block" "none")}}
        [:li [:a {:href @(rf/subscribe [:sample/download-html-url])}
              [:img.icon.download-icon {:src "/icons/editor/download-html.svg"}]
              "HTML with links"]]
        [:li [:a {:href @(rf/subscribe [:sample/download-zip-url])}
              [:img.icon.download-icon {:src "/icons/editor/download-zip.svg"}]
              "ZIP with files"]]]]

      [search/search-input]
      ]]

    [:ul.nav.navbar-nav.navbar-right
     [:li.dropdown
      [:button.btn.btn-link.dropdown-toggle {;:data-toggle   "dropdown"
                                             :role          "button"
                                             :aria-haspopup "true"
                                             :aria-expanded "false"
                                             :on-click      #(rf/dispatch [:create-menu/show])} "Create"
       [:span.caret]]
      [:ul.dropdown-menu
       {:style {:display (if @(rf/subscribe [:create-menu/show]) "block" "none")}}
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


;(defn send-form []
;  [:form#run-form
;   {:style  {:display "none"}
;    :action "/run"
;    :target "result-iframe"
;    :method "POST"}
;   [:input {:name  "code"
;            :value @(rf/subscribe [:sample/code])
;            :type  "hidden"}]
;   [:input {:name  "markup"
;            :value @(rf/subscribe [:sample/markup])
;            :type  "hidden"}]
;   [:input {:name  "style"
;            :value @(rf/subscribe [:sample/style])
;            :type  "hidden"}]
;   [:input {:name  "styles"
;            :value @(rf/subscribe [:sample/styles])
;            :type  "hidden"}]
;   [:input {:name  "scripts"
;            :value @(rf/subscribe [:sample/scripts])
;            :type  "hidden"}]])


(defn app []
  [:div
   ;[send-form]
   [navbar]
   [editors/editors]
   [tips/tips]
   [settings-window/settings-window]
   [export-window/export-window]
   [sidemenu/view]
   [modal-view/window]])
