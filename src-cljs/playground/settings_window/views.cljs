(ns playground.settings-window.views
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]
            [playground.data.external-resources :as external-resources]))


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

   ;; TODO: wait datasests texts
   ;[:li {:class (when @(rf/subscribe [:settings/datasets-tab?]) "active")}
   ; [:a {:href     "javascript:;"
   ;      :role     "button"
   ;      :on-click #(rf/dispatch [:settings/datasets-tab])} "Data Sets"]]
   ])


(defn general-tab []
  [:div.general-tab.content
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
                             :style         {:max-height @(rf/subscribe [:settings.general-tab/description-height])}
                             :default-value @(rf/subscribe [:sample/description])
                             :on-change     #(rf/dispatch [:settings/change-desc (-> % .-target .-value)])}]]
   [:div.form-group
    [:label "Tags"]
    [:div.tags-box
     (for [tag @(rf/subscribe [:settings/tags])]
       ^{:key (:name tag)}
       [:a.tag {:on-click #(do
                             ;(rf/dispatch [:settings/select-tag (:name tag)])
                             (.focus (.getElementById js/document "tags-input")))
                :class    (when (:selected tag) "selected")}
        [:span (:name tag)]
        [:span.glyphicon.glyphicon-remove
         {:on-click #(rf/dispatch [:settings/remove-tag (:name tag)])}]])
     [:input.form-control {:id          "tags-input"
                           :placeholder "Add new tag"
                           :on-key-down #(do                ;; TODO: move to somewhere else?
                                           (when (= 13 (.-keyCode %))
                                             (rf/dispatch [:settings/add-tag (-> % .-target .-value)])
                                             (set! (-> % .-target .-value) ""))
                                           (when (and (= 9 (.-keyCode %))
                                                      (not= (-> % .-target .-value) ""))
                                             (rf/dispatch [:settings/add-tag (-> % .-target .-value)])
                                             (set! (-> % .-target .-value) "")
                                             (.preventDefault %))
                                           (when (and (= 8 (.-keyCode %))
                                                      (= (-> % .-target .-value) ""))
                                             (rf/dispatch [:settings/tags-backspace])))}]]]

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


(defn javascript-tab []
  [:div.javascript-tab.content
   [:p.section-label "Scripts"]
   [:div.scripts-box
    (for [script @(rf/subscribe [:sample/scripts])]
      ^{:key script}
      [:div.script
       [:a {:href script :target "_blank"}
        [:span.glyphicon.glyphicon-align-justify]
        [:div.in-box
         [:span.url script]
         [:span.glyphicon.glyphicon-remove {:on-click #(do
                                                         (.preventDefault %)
                                                         (rf/dispatch [:settings/remove-script script]))}]]]])]
   [:div.line
    [:input.form-control.script-input {:id          "script-input"
                                       :placeholder "Add new script"
                                       :on-key-down #(when (= 13 (.-keyCode %))
                                                       (rf/dispatch [:settings/add-script
                                                                     (-> % .-target .-value)])
                                                       (set! (.-value (.getElementById js/document "script-input")) ""))}]

    [:button.ac-btn.add-btn {:type     "button"
                             :on-click #(do (rf/dispatch [:settings/add-script
                                                          (.-value (.getElementById js/document "script-input"))])
                                            (set! (.-value (.getElementById js/document "script-input")) ""))}
     "Add"]]

   [:p.section-label.quick-add "Quick Add"]

   [:div.row
    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-bin"} "AnyChart Binaries"]
      [:div.line
       [:select.form-control {:id        "settings-select-bin"
                              :on-change #(rf/dispatch [:settings.external-resources/binaries-select (-> % .-target .-value)])}
        [:optgroup {:label "Chart Types"}
         (for [res external-resources/chart-types-modules]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Features"}
         (for [res external-resources/feature-modules]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Bundles"}
         (for [res external-resources/bundle-modules]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Misc"}
         (for [res external-resources/misc-modules]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]]
       (if @(rf/subscribe [:settings.external-resources/added-js? :binary])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :binary])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :binary])} "Add"])]]
     ]


    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-theme"} "AnyChart Themes"]
      [:div.line
       [:select.form-control {:id        "settings-select-theme"
                              :on-change #(rf/dispatch [:settings.external-resources/themes-select (-> % .-target .-value)])}
        (for [res external-resources/themes]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :theme])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :theme])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :theme])} "Add"])]]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-locale"} "AnyChart Locales"]
      [:div.line
       [:select.form-control {:id        "settings-select-locale"
                              :on-change #(rf/dispatch [:settings.external-resources/locales-select (-> % .-target .-value)])}
        (for [res external-resources/locales]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :locale])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :locale])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :locale])} "Add"])]]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-map"} "AnyChart Geo Data"]
      [:div.line
       [:select.form-control {:id        "settings-select-map"
                              :on-change #(rf/dispatch [:settings.external-resources/maps-select (-> % .-target .-value)])}
        ;(for [res external-resources/maps]
        ;  ^{:key res} [:option {:value (:url res)} (:name res)])
        (for [res external-resources/maps-html]
          [:optgroup {:label (:name res)}
           (for [item (:items res)]
             ^{:key item} [:option {:value (:url item)} (:name item)])])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :map])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :map])} "Remove"]
         [:button.ac-btn.add-btn. {:type     "button"
                                   :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :map])} "Add"])]]
     ]]])


(defn css-tab []
  [:div.javascript-tab.css-tab.content

   [:p.section-label "Styles"]
   [:div.scripts-box
    (for [style @(rf/subscribe [:sample/styles])]
      ^{:key style}
      [:div.script
       [:a {:href style :target "_blank"}
        [:span.glyphicon.glyphicon-align-justify]
        [:div.in-box
         [:span.url style]
         [:span.glyphicon.glyphicon-remove {:on-click #(do
                                                         (.preventDefault %)
                                                         (rf/dispatch [:settings/remove-style style]))}]]]])]
   [:div.line
    [:input.form-control.script-input {:id          "style-input"
                                       :placeholder "Add new style"
                                       :on-key-down #(when (= 13 (.-keyCode %))
                                                       (rf/dispatch [:settings/add-style
                                                                     (-> % .-target .-value)])
                                                       (set! (.-value (.getElementById js/document "style-input")) ""))}]

    [:button.ac-btn.add-btn {:type     "button"
                             :on-click #(do (rf/dispatch [:settings/add-style
                                                          (.-value (.getElementById js/document "style-input"))])
                                            (set! (.-value (.getElementById js/document "style-input")) ""))}
     "Add"]]

   [:p.section-label.quick-add "Quick Add"]

   [:div.row
    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-bin"} "AnyChart CSS"]
      [:div.line
       [:select.form-control {:id        "settings-select-bin"
                              :on-change #(rf/dispatch [:settings.external-resources/css-select (-> % .-target .-value)])}
        (for [res external-resources/css]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-css? :css])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-css-by-type :css])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-css-by-type :css])} "Add"])]]]]])


(defn datasets-tab []
  [:div.datasets-tab.content
   [:div.row
    (for [dataset @(rf/subscribe [:datasets])]
      ^{:key (:name dataset)}
      [:div.col-sm-4
       [:div.item
        [:div.hover-box
         [:img {:src (:logo dataset)}]
         [:span.title (:title dataset)]
         [:p.info (:description dataset)]]
        [:div.usage-sample-line
         [:a.usage-sample {:href   (:sample dataset)
                           :target "_blank"} "Usage Sample"]
         [:a.question {:href (:sample dataset)}]]
        (if (:added dataset)
          [:div.added-label
           [:span.glyphicon.glyphicon-ok]
           [:span "Already added"]]
          [:input.quick-add-btn {:type     "button"
                                 :value    "Quick Add"
                                 :on-click #(rf/dispatch [:settings/add-dataset dataset])}])
        ;(for [tag (:tags dataset)]
        ;  ^{:key tag} [:span.label.label-primary.tag tag])
        ]
       ]
      )]])


(defn settings-window []
  (when @(rf/subscribe [:settings/show])
    [:div.settings-window.hide-outside
     [nav-menu]

     (when @(rf/subscribe [:settings/general-tab?])
       [general-tab])

     (when @(rf/subscribe [:settings/javascript-tab?])
       [javascript-tab])

     (when @(rf/subscribe [:settings/css-tab?])
       [css-tab])

     (when @(rf/subscribe [:settings/datasets-tab?])
       [datasets-tab])]))
