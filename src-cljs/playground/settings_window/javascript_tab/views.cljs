(ns playground.settings-window.javascript-tab.views
  (:require [re-frame.core :as rf]
            [playground.settings-window.external-resources.views :as version-select]
            [playground.data.consts :as consts]
            [reagent.core :as reagent]))


(defn scripts-box []
  (reagent/create-class
    {:component-did-mount #(let [el (.getElementById js/document "scripts-box")]
                             (.create js/Sortable el (clj->js {:animation 150
                                                               :draggable ".script"
                                                               :handle    ".icon-move"
                                                               :onEnd     (fn [^js/SortableOnEndEvent e]
                                                                            (rf/dispatch [:settings/update-scripts-order
                                                                                          (.-oldIndex e) (.-newIndex e)]))})))
     :reagent-render      (fn []
                            [:div#scripts-box.scripts-box
                             (for [[idx {script :script warning :warning}] (map-indexed (fn [idx script] [idx script])
                                                                                        @(rf/subscribe [:settings.javascript-tab/correct-scripts]))]
                               ^{:key (str script "-" idx)}
                               [:div.script
                                [:span.script-box
                                 [:i.fas.fa-bars.icon-move]
                                 [:div.in-box
                                  [:div.input-height-box {:title warning}
                                   [:span.height-line script]
                                   [:input.url {:type          "text"
                                                :default-value script
                                                :on-blur       #(rf/dispatch [:settings/edit-script (-> % .-target .-value) idx])
                                                :on-key-down   #(when (= 13 (.-keyCode %))
                                                                  (.blur (-> % .-target))
                                                                  (rf/dispatch [:settings/edit-script (-> % .-target .-value) idx]))
                                                ;:on-change   #(rf/dispatch [:settings/edit-script (-> % .-target .-value) idx])
                                                }]]
                                  [:i.fas.fa-times.icon-close {:on-click #(do
                                                                            (.preventDefault %)
                                                                            (rf/dispatch [:settings/remove-script script]))}]]
                                 (when warning
                                   [:i.fas.fa-exclamation-triangle.icon-warning {:title warning}])
                                 ]
                                ])]
                            )}))


(defn javascript-tab []
  [:div.javascript-tab.content
   [:p.section-label "Scripts"
    [:span.question-small.tooltiped
     [:span.tooltip-box consts/scripts-title]]]

   [scripts-box]

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

   ;[:p.section-label.quick-add "Quick Add"]
   [:p.section-label.quick-add ""]
   [version-select/version-select]

   [:div.row
    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-bin"} "AnyChart Binaries"
       [:span.question-small.tooltiped
        [:span.tooltip-box consts/anychart-binaries-title
         [:a {:href   "https://docs.anychart.com/Quick_Start/Modules"
              :target "_blank"} "Read more >>"]]]]
      (let [loading @(rf/subscribe [:settings.external-resources/loading])
            groups @(rf/subscribe [:settings.external-resources/binaries-groups])
            disabled (or loading (empty? groups))]
        [:div.line
         [:select.form-control {:id        "settings-select-bin"
                                :disabled  disabled
                                :value     @(rf/subscribe [:settings.external-resources/selected-resource :binary])
                                :on-change #(rf/dispatch [:settings.external-resources/binaries-select (-> % .-target .-value)])}
          (for [res groups]
            (when (seq (:items res))
              ^{:key (:name res)}
              [:optgroup {:label (:name res)}
               (for [item (:items res)]
                 ^{:key item} [:option {:value (:url item)} (:name item)])]))
          ]
         (if @(rf/subscribe [:settings.external-resources/added-js? :binary])
           [:button.ac-btn.remove-btn {:type     "button"
                                       :disabled disabled
                                       :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :binary])} "Remove"]
           [:button.ac-btn.add-btn {:type     "button"
                                    :disabled disabled
                                    :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :binary])} "Add"])])]
     ]


    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-theme"} "AnyChart Themes"
       [:span.question-small.tooltiped
        [:span.tooltip-box consts/anychart-themes-title
         [:a {:href   "https://docs.anychart.com/Appearance_Settings/Themes"
              :target "_blank"} "Read more >>"]]]]
      (let [themes @(rf/subscribe [:settings.external-resources/themes])
            loading @(rf/subscribe [:settings.external-resources/loading])
            disabled (or loading (empty? themes))]
        [:div.line
         [:select.form-control {:id        "settings-select-theme"
                                :disabled  disabled
                                :value     @(rf/subscribe [:settings.external-resources/selected-resource :theme])
                                :on-change #(rf/dispatch [:settings.external-resources/themes-select (-> % .-target .-value)])}
          (for [res themes]
            ^{:key res} [:option {:value (:url res)} (:name res)])]
         (if @(rf/subscribe [:settings.external-resources/added-js? :theme])
           [:button.ac-btn.remove-btn {:type     "button"
                                       :disabled disabled
                                       :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :theme])} "Remove"]
           [:button.ac-btn.add-btn {:type     "button"
                                    :disabled disabled
                                    :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :theme])} "Add"])])]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-locale"} "AnyChart Locales"
       [:span.question-small.tooltiped
        [:span.tooltip-box consts/anychart-locales-title
         [:a {:href   "https://docs.anychart.com/Common_Settings/Localization"
              :target "_blank"} "Read more >>"]]]]
      (let [loading @(rf/subscribe [:settings.external-resources/loading])
            locales @(rf/subscribe [:settings.external-resources/locales])
            disabled (or loading (empty? locales))]
        [:div.line
         [:select.form-control {:id        "settings-select-locale"
                                :disabled  disabled
                                :value     @(rf/subscribe [:settings.external-resources/selected-resource :locale])
                                :on-change #(rf/dispatch [:settings.external-resources/locales-select (-> % .-target .-value)])}
          (for [res locales]
            ^{:key res} [:option {:value (:url res)} (:name res)])]
         (if @(rf/subscribe [:settings.external-resources/added-js? :locale])
           [:button.ac-btn.remove-btn {:type     "button"
                                       :disabled disabled
                                       :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :locale])} "Remove"]
           [:button.ac-btn.add-btn {:type     "button"
                                    :disabled disabled
                                    :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :locale])} "Add"])])]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-map"} "AnyChart Geo Data"
       [:span.question-small.tooltiped
        [:span.tooltip-box consts/anychart-geo-title
         [:a {:href   "https://docs.anychart.com/Maps/Maps_List"
              :target "_blank"} "Read more >>"]]]]
      (let [loading @(rf/subscribe [:settings.external-resources/loading])
            groups @(rf/subscribe [:settings.external-resources/maps-groups])
            disabled (or loading (empty? groups))]
        [:div.line
         [:select.form-control {:id        "settings-select-map"
                                :disabled  disabled
                                :value     @(rf/subscribe [:settings.external-resources/selected-resource :map])
                                :on-change #(rf/dispatch [:settings.external-resources/maps-select (-> % .-target .-value)])}
          (for [res groups]
            (when (seq (:items res))
              ^{:key (:name res)}
              [:optgroup {:label (:name res)}
               (for [item (:items res)]
                 ^{:key item} [:option {:value (:url item)} (:name item)])]))]
         (if @(rf/subscribe [:settings.external-resources/added-js? :map])
           [:button.ac-btn.remove-btn {:type     "button"
                                       :disabled disabled
                                       :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :map])} "Remove"]
           [:button.ac-btn.add-btn. {:type     "button"
                                     :disabled disabled
                                     :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :map])} "Add"])])]
     ]]])