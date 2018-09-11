(ns playground.export-window.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn embed-editor []
  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:embed/create-iframe-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-iframe-editor {:class "editor-box editor-box-embed"}])}))

(defn iframe-internal-editor []
  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:embed/create-internal-iframe-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-internal-iframe-editor {:class "editor-box editor-box-embed"}])}))

(defn plain-html-editor []
  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:embed/create-plain-html-editor]))
                         :reagent-render      (fn []
                                                [:div#embed-plain-html-editor {:class "editor-box editor-box-embed"}])}))


(defn export-window []
  (when @(rf/subscribe [:embed/show])
    [:div.dropdown-window.export-window.hide-outside

     [:ul.nav.nav-tabs.settings-tabs
      [:li {:class (when @(rf/subscribe [:embed/embed-tab?]) "active")}
       [:a {:href     "javascript:;"
            :on-click #(rf/dispatch [:embed/embed-tab])} "Embed"]]
      ;; TODO: redesign download tab
      ;[:li {:class (when @(rf/subscribe [:embed/download-tab?]) "active")}
      ; [:a {:href     "javascript:;"
      ;      :on-click #(rf/dispatch [:embed/download-tab])} "Download"]]
      ]

     (when @(rf/subscribe [:embed/embed-tab?])
       [:div
        [:div.content
         [:p.intro "To embed into a web page, copy and paste one of the code snippets below."]

         [:form.form-inline.props-box

          [:div.form-group
           [:label {:for "id-export-prop"} "ID"]
           [:input.form-control {:id        "id-export-prop"
                                 :style     {:width "74px"}
                                 :value     @(rf/subscribe [:embed.props/id])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-id (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "class-export-prop"} "class"]
           [:input.form-control {:id        "class-export-prop"
                                 :style     {:width "112px"}
                                 :value     @(rf/subscribe [:embed.props/class])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-class (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "width-export-prop"} "width"]
           [:input.form-control {:id        "width-export-prop"
                                 :style     {:width "57px"}
                                 :value     @(rf/subscribe [:embed.props/width])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-width (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "height-export-prop"} "height"]
           [:input.form-control {:id        "height-export-prop"
                                 :style     {:width "57px"}
                                 :value     @(rf/subscribe [:embed.props/height])
                                 :type      "text"
                                 :on-change #(rf/dispatch [:embed.props/change-height (-> % .-target .-value)])}]]
          ]
         ]


        [:ul.nav.nav-tabs.settings-tabs.sub-tabs
         [:li {:class (when @(rf/subscribe [:embed/html-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :role     "button"
               :on-click #(rf/dispatch [:embed/html-sub-tab])}
           [:span "Plain HTML"]]]

         [:li {:class (when @(rf/subscribe [:embed/iframe-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :role     "button"
               :on-click #(rf/dispatch [:embed/iframe-sub-tab])}
           [:span "HTML iframe"]]]

         [:li {:class (when @(rf/subscribe [:embed/iframe2-sub-tab?]) "active")}
          [:a {:href     "javascript:;"
               :role     "button"
               :on-click #(rf/dispatch [:embed/iframe2-sub-tab])}
           [:span "HTML iframe (auto update)"]]]]

        (when @(rf/subscribe [:embed/html-sub-tab?])
          [:div.content
           [:p.sub-intro
            "Make sure that IDs of HTML elements and names of CSS styles defined in the sample do not interfere with the page you are embedding this code into."]
           [plain-html-editor]
           [:input.ac-btn.add-btn {:id    "copy-embed-plain-html"
                                   :type  "button"
                                   :value "Copy"}]])

        (when @(rf/subscribe [:embed/iframe-sub-tab?])
          [:div.content
           [:p.sub-intro
            "This option protects your page content from the IDs and CSS used in the sample but may increase the page initial loading time."]
           [iframe-internal-editor]
           [:input.ac-btn.add-btn {:id    "copy-embed-internal-iframe"
                                   :type  "button"
                                   :value "Copy"}]])

        (when @(rf/subscribe [:embed/iframe2-sub-tab?])
          [:div.content
           [:p.sub-intro
            "This option protects your page content from the IDs and CSS used in the sample but may increase the page initial loading time."]
           [embed-editor]
           [:input.ac-btn.add-btn {:id    "copy-embed-iframe"
                                   :type  "button"
                                   :value "Copy"}]])
        ])

     (when @(rf/subscribe [:embed/download-tab?])
       [:div.content
        [:div                                               ;.form-group
         [:a.ac-btn.add-btn {:role "button"
                             :href @(rf/subscribe [:embed/download-html-link])}
          [:span.glyphicon.glyphicon-download-alt {:aria-hidden true}]
          " HTML"]]])

     ]))
