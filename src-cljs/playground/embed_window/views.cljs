(ns playground.embed-window.views
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]
            [reagent.core :as reagent]))

(defn embed-editor []
  (reagent/create-class {:component-did-mount #(do (utils/log "Embed iframe editor did mount!")
                                                   (rf/dispatch [:embed/create-iframe-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-iframe-editor {:class "editor-box editor-box-embed"}])}))

(defn iframe-internal-editor []
  (reagent/create-class {:component-did-mount #(do (utils/log "Embed internal-iframe editor did mount!")
                                                   (rf/dispatch [:embed/create-internal-iframe-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-internal-iframe-editor {:class "editor-box editor-box-embed"}])}))

(defn plain-html-editor []
  (reagent/create-class {:component-did-mount #(do (utils/log "Embed plain-html editor did mount!")
                                                   (rf/dispatch [:embed/create-plain-html-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-plain-html-editor {:class "editor-box editor-box-embed"}])}))





(defn embed-window []
  (when @(rf/subscribe [:embed/show])
    [:div.settings-window.embed-window.hide-outside

     [:ul.nav.nav-tabs.settings-tabs
      [:li {:class (when @(rf/subscribe [:embed/embed-tab?]) "active")}
       [:a {:href     "javascript:;"
            :on-click #(rf/dispatch [:embed/embed-tab])} "Embed"]]

      [:li {:class (when @(rf/subscribe [:embed/download-tab?]) "active")}
       [:a {:href     "javascript:;"
            :on-click #(rf/dispatch [:embed/download-tab])} "Download"]]]

     (when @(rf/subscribe [:embed/embed-tab?])
       [:div
        [:div.content
         [:p.intro "To place the chart in a web page, copy one of the code snippets below. We recommend using the first option as far as it doesn't bother your page with using external services to load the chart."]

         [:form.form-inline

          [:div.form-group
           [:label "ID"]
           [:input.form-control {:style     {:width "74px"}
                                 :value     @(rf/subscribe [:embed.props/id])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-id (-> % .-target .-value)])}]]
          [:div.form-group
           [:label "class"]
           [:input.form-control {:style     {:width "112px"}
                                 :value     @(rf/subscribe [:embed.props/class])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-class (-> % .-target .-value)])}]]
          [:div.form-group
           [:label "width"]
           [:input.form-control {:style     {:width "57px"}
                                 :value     @(rf/subscribe [:embed.props/width])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-width (-> % .-target .-value)])}]]
          [:div.form-group
           [:label "height"]
           [:input.form-control {:style     {:width "57px"}
                                 :value     @(rf/subscribe [:embed.props/height])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-height (-> % .-target .-value)])}]]
          ]
         ]


        [:ul.nav.nav-tabs.settings-tabs.sub-tabs
         [:li {:class (when @(rf/subscribe [:embed/html-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:embed/html-sub-tab])}
           [:span "Plain HTML"]]]

         [:li {:class (when @(rf/subscribe [:embed/iframe-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:embed/iframe-sub-tab])}
           [:span "HTML iframe"]]]

         [:li {:class (when @(rf/subscribe [:embed/iframe2-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :on-click #(rf/dispatch [:embed/iframe2-sub-tab])}
           [:span "HTML iframe (auto update)"]]]]

        (when @(rf/subscribe [:embed/html-sub-tab?])
          [:div.content
           [:p.sub-intro
            "Please, make sure that IDs of HTML elements and CSS styles defined in the sample does not corrupt your page content."]
           [plain-html-editor]
           [:input.btn.btn-primary {:id    "copy-embed-plain-html"
                                    :type  "button"
                                    :value "Copy"}]])

        (when @(rf/subscribe [:embed/iframe-sub-tab?])
          [:div.content
           [:p.sub-intro
            "This option doesn't use external resources and protect your page content from the ID's and CSS used in the sample, but usage of HTML iframe is not convenient to use from the page loading speed perspective."]
           [iframe-internal-editor]
           [:input.btn.btn-primary {:id    "copy-embed-internal-iframe"
                                    :type  "button"
                                    :value "Copy"}]])

        (when @(rf/subscribe [:embed/iframe2-sub-tab?])
          [:div.content
           [:p.sub-intro
            "The advantage of this option is auto-update of the sample embedded on your page then you're updating the sample on playground."]
           [embed-editor]
           [:input.btn.btn-primary {:id    "copy-embed-iframe"
                                    :type  "button"
                                    :value "Copy"}]])
        ])

     (when @(rf/subscribe [:embed/download-tab?])
       [:div.content
        [:div                                               ;.form-group
         [:a.btn.btn-primary {:role "button"
                              :href @(rf/subscribe [:embed/download-html-link])}
          [:span.glyphicon.glyphicon-download-alt {:aria-hidden true}]
          " HTML"]]])

     ]))
