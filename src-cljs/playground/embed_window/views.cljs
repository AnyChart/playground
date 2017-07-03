(ns playground.embed-window.views
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]
            [reagent.core :as reagent]))

(defn embed-editor []
  (reagent/create-class {:component-did-mount #(do (utils/log "Embed iframe editor did mount!")
                                                   (rf/dispatch [:embed/create-iframe-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-iframe-editor {:class "editor-box"}])}))


(defn plain-html-editor []
  (reagent/create-class {:component-did-mount #(do (utils/log "Embed plain-html editor did mount!")
                                                   (rf/dispatch [:embed/create-plain-html-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-plain-html-editor {:class "editor-box"
                                                                               :style {:height "126px"}}])}))


(defn embed-window []
  (when @(rf/subscribe [:embed/show])
    [:div.settings-window
     [:div.settings-window-background {:on-click #(rf/dispatch [:embed/hide])}]
     [:div.settings-window-container

      [:ul.nav.nav-tabs.settings-tabs
       [:li {:class (when @(rf/subscribe [:embed/embed-tab?]) "active")}
        [:a {:href     "javascript:;"
             :on-click #(rf/dispatch [:embed/embed-tab])} "Embed"]]

       [:li {:class (when @(rf/subscribe [:embed/download-tab?]) "active")}
        [:a {:href     "javascript:;"
             :on-click #(rf/dispatch [:embed/download-tab])} "Download"]]]

      [:form

       (when @(rf/subscribe [:embed/embed-tab?])
         [:div
          [:p "To place the chart in a web page, copy one of the code snippets below. We recommend using the first option as far as it doesn't bother your page with using external services to load the chart."]

          [:div.form-group
           [:label {:for "settings-desc"} "Plain html"]
           [:div "Please, make sure that IDs of HTML elements and CSS styles defined in the sample does not corrupt your page content."]
           [plain-html-editor]
           [:input.btn.btn-primary {:id    "copy-embed-plain-html"
                                    :type  "button"
                                    :value "Copy"}]]

          [:div.form-group
           [:label {:for "settings-desc"} "Iframe with external source"]
           [:div "The advantage of this option is auto-update of the sample embedded on your page then you're updating the sample on playground."]
           [embed-editor]
           [:input.btn.btn-primary {:id    "copy-embed-iframe"
                                    :type  "button"
                                    :value "Copy"}]]
          ])

       (when @(rf/subscribe [:embed/download-tab?])
         [:div
          [:div.form-group
           [:a.btn.btn-primary {:role "button"
                                :href @(rf/subscribe [:embed/download-html-link])} "HTML"]]])


       [:button.btn.btn-default {:type     "button"
                                 :on-click #(rf/dispatch [:embed/hide])} "Close"]]

      ]]))
