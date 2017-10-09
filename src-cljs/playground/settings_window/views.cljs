(ns playground.settings-window.views
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]
            [playground.settings-window.data :as external-resources]))


(defn nav-menu []
  [:ul.nav.nav-tabs.settings-tabs
   [:li {:class (when @(rf/subscribe [:settings/general-tab?]) "active")}
    [:a {:href     "javascript:;"
         :role     "button"
         :on-click #(rf/dispatch [:settings/general-tab])} "General"]]

   [:li {:class (when @(rf/subscribe [:settings/javascript-tab?]) "active")}
    [:a {:href     "javascript:;"
         :role     "button"
         :on-click #(rf/dispatch [:settings/javascript-tab])} "JavaScript"]]

   [:li {:class (when @(rf/subscribe [:settings/css-tab?]) "active")}
    [:a {:href     "javascript:;"
         :role     "button"
         :on-click #(rf/dispatch [:settings/css-tab])} "CSS"]]

   [:li {:class (when @(rf/subscribe [:settings/data-sets-tab?]) "active")}
    [:a {:href     "javascript:;"
         :role     "button"
         :on-click #(rf/dispatch [:settings/data-sets-tab])} "Data Sets"]]])


(defn general-tab []
  [:div.general-tab
   [:div.form-group
    [:label {:for "settings-name"} "Name"]
    [:input.form-control {:id            "settings-name"
                          :default-value @(rf/subscribe [:sample/name])
                          :on-change     #(rf/dispatch [:settings/change-name (-> % .-target .-value)])}]]
   [:div.form-group
    [:label {:for "settings-short-desc"} "Short Description"]
    [:textarea.form-control {:id            "settings-short-desc"
                             :default-value @(rf/subscribe [:sample/short-description])
                             :on-change     #(rf/dispatch [:settings/change-short-desc (-> % .-target .-value)])}]]
   [:div.form-group
    [:label {:for "settings-desc"} "Description"]
    [:textarea.form-control {:id            "settings-desc"
                             :default-value @(rf/subscribe [:sample/description])
                             :on-change     #(rf/dispatch [:settings/change-desc (-> % .-target .-value)])}]]
   [:div.form-group
    [:label "Tags"]
    [:div.tags-box
     (for [tag @(rf/subscribe [:sample/tags])]
       ^{:key tag}
       [:a.tag {}
        [:span tag]
        [:span.glyphicon.glyphicon-remove
         {:on-click #(rf/dispatch [:settings/remove-tag tag])}]])
     [:input.form-control {:placeholder "Add new tag"
                           :on-key-down #(when (= 13 (.-keyCode %))
                                           (rf/dispatch [:settings/add-tag (-> % .-target .-value)])
                                           (set! (-> % .-target .-value) ""))}]]]
   ;[:div.form-inline
   ; {:style {:padding-right "10px"}}
   ;
   ; [:div.form-group
   ;  [:label {:for "settings-markup-type"} "Markup type"]
   ;  [:select.form-control {:id            "settings-markupt-type"
   ;                         :default-value @(rf/subscribe [:markup-type])}
   ;   [:option "HTML"]
   ;   [:option "Slim"]
   ;   [:option "Pug"]]]
   ;
   ; [:div.form-group
   ;  [:label {:for "settings-style-type"} "Style type"]
   ;  [:select.form-control {:id            "settings-style-type"
   ;                         :default-value @(rf/subscribe [:style-type])}
   ;   [:option "CSS"]
   ;   [:option "Sass"]
   ;   [:option "LESS"]]]
   ;
   ; [:div.form-group
   ;  [:label {:for "settings-code-type"} "Code type"]
   ;  [:select.form-control {:id            "settings-code-type"
   ;                         :default-value @(rf/subscribe [:code-type])}
   ;   [:option "JavaScript"]
   ;   [:option "CoffeeScript"]
   ;   [:option "TypeScript"]]]]

   ])


(defn settings-window []
  (when @(rf/subscribe [:settings/show])
    [:div.settings-window.hide-outside
     [nav-menu]
     [:form.content

      (when @(rf/subscribe [:settings/general-tab?])
        [general-tab])

      (when @(rf/subscribe [:settings/javascript-tab?])
        [:div.javascript-tab
         [:p.section-label "Scripts"]
         (for [script @(rf/subscribe [:sample/scripts])]
           ^{:key script}
           [:div.settings-resource
            [:div.title [:a {:href script :target "_blank"} script]]
            [:button.btn.btn-primary.btn-xs {:type     "button"
                                             :on-click #(rf/dispatch [:settings/remove-script script])}
             [:span.glyphicon.glyphicon-remove]]])

         [:p.section-label "Quick Add"]

         [:div.row
          [:div.col-sm-6
           [:div.form-group
            [:label {:for "settings-select-bin"} "Binaries"]
            [:div {:style {:display "flex"}}
             [:select.form-control {:id        "settings-select-bin"
                                    :on-change #(rf/dispatch [:settings.external-resources/binaries-select (-> % .-target .-value)])}
              (for [res external-resources/binaries]
                ^{:key res} [:option {:value (:url res)} (:name res)])]
             (if @(rf/subscribe [:settings.external-resources/added? :binary])
               [:button.btn.btn-primary {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/remove-by-type :binary])} "Remove"]
               [:button.btn.btn-success {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/add-by-type :binary])} "Add"])]]
           ]


          [:div.col-sm-6
           [:div.form-group
            [:label {:for "settings-select-theme"} "Themes"]
            [:div {:style {:display "flex"}}
             [:select.form-control {:id        "settings-select-theme"
                                    :on-change #(rf/dispatch [:settings.external-resources/themes-select (-> % .-target .-value)])}
              (for [res external-resources/themes]
                ^{:key res} [:option {:value (:url res)} (:name res)])]
             (if @(rf/subscribe [:settings.external-resources/added? :theme])
               [:button.btn.btn-primary {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/remove-by-type :theme])} "Remove"]
               [:button.btn.btn-success {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/add-by-type :theme])} "Add"])]]
           ]

          [:div.col-sm-6
           [:div.form-group
            [:label {:for "settings-select-locale"} "Locales"]
            [:div {:style {:display "flex"}}
             [:select.form-control {:id        "settings-select-locale"
                                    :on-change #(rf/dispatch [:settings.external-resources/locales-select (-> % .-target .-value)])}
              (for [res external-resources/locales]
                ^{:key res} [:option {:value (:url res)} (:name res)])]
             (if @(rf/subscribe [:settings.external-resources/added? :locale])
               [:button.btn.btn-primary {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/remove-by-type :locale])} "Remove"]
               [:button.btn.btn-success {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/add-by-type :locale])} "Add"])]]
           ]

          [:div.col-sm-6
           [:div.form-group
            [:label {:for "settings-select-map"} "Maps"]
            [:div {:style {:display "flex"}}
             [:select.form-control {:id        "settings-select-map"
                                    :on-change #(rf/dispatch [:settings.external-resources/maps-select (-> % .-target .-value)])}
              (for [res external-resources/maps]
                ^{:key res} [:option {:value (:url res)} (:name res)])]
             (if @(rf/subscribe [:settings.external-resources/added? :map])
               [:button.btn.btn-primary {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/remove-by-type :map])} "Remove"]
               [:button.btn.btn-success {:type     "button"
                                         :on-click #(rf/dispatch [:settings.external-resources/add-by-type :map])} "Add"])]]
           ]
          ]
         ])

      (when @(rf/subscribe [:settings/css-tab?])
        [:div.form-group
         [:label {:for "settings-styles"} "Styles"]
         (for [style @(rf/subscribe [:sample/styles])]
           ^{:key style}
           [:div.settings-resource
            [:div.title [:a {:href style :target "_blank"} style]]
            [:button.btn.btn-primary.btn-xs {:type     "button"
                                             :on-click #(rf/dispatch [:settings/remove-style style])}
             [:span.glyphicon.glyphicon-remove]]])]
        )

      (when @(rf/subscribe [:settings/data-sets-tab?])
        [:div.datasets-tab

         [:div.row
          (for [data-set @(rf/subscribe [:data-sets])]
            ^{:key (:name data-set)}
            [:div.col-sm-4
             [:div.item
              [:img {:src (:logo data-set)}]
              [:span.title (:title data-set)]
              [:p (:description data-set)]
              [:a.usage-sample {:href   (:sample data-set)
                                :target "_blank"} "Usage Sample"]
              [:input.btn.btn-primary.btn-xs.usage-sample-button {:type     "button"
                                                                  :value    "Quick Add"
                                                                  :on-click #(rf/dispatch [:settings/add-dataset data-set])}
               ]
              ]
             ]




            ;[:div.row.data-sets-item
            ; [:div.col-md-2.data-sets-item-icon
            ;  [:img {:src (:logo data-set)}]]
            ; [:div.col-md-7
            ;  [:h5 (:title data-set)]
            ;  [:p (:description data-set)]
            ;  (for [tag (:tags data-set)]
            ;    ^{:key tag} [:span.label.label-primary.tag tag])]
            ; [:div.col-md-3
            ;  [:a.btn.btn-primary.btn-xs.usage-sample-button {:href   (:sample data-set)
            ;                                                  :target "_blank"} "Usage Sample"]
            ;  [:a.btn.btn-success.btn-xs.usage-sample-button {:href     "javascript:;"
            ;                                                  :on-click #(rf/dispatch [:settings/add-dataset data-set])}
            ;   "Quick Add"]]]

            )]])

      ]

     ]))
