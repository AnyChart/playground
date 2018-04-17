(ns playground.settings-window.javascript-tab.views
  (:require [re-frame.core :as rf]
            [playground.settings-window.external-resources.views :as version-select]
            [reagent.core :as reagent]))


(defn scripts-box []
  (reagent/create-class
    {:component-did-mount #(let [el (.getElementById js/document "scripts-box")]
                             (.create js/Sortable el (clj->js {:animation 150
                                                               :draggable ".script"
                                                               :handle    ".glyphicon-align-justify"
                                                               :onEnd     (fn [^js/SortableOnEndEvent e]
                                                                            (rf/dispatch [:settings/update-scripts-order
                                                                                          (.-oldIndex e) (.-newIndex e)]))})))
     :reagent-render      (fn []
                            [:div#scripts-box.scripts-box
                             (for [[idx script] (map-indexed (fn [idx script] [idx script])
                                                             @(rf/subscribe [:sample/scripts]))]
                               ^{:key (str script "-" idx)}
                               [:div.script
                                [:span.script-box
                                 [:span.glyphicon.glyphicon-align-justify]
                                 [:div.in-box
                                  [:div.input-height-box
                                   [:span.height-line script]
                                   [:input.url {:type          "text"
                                                :default-value script
                                                :on-blur       #(rf/dispatch [:settings/edit-script (-> % .-target .-value) idx])
                                                :on-key-down   #(when (= 13 (.-keyCode %))
                                                                  (.blur (-> % .-target))
                                                                  (rf/dispatch [:settings/edit-script (-> % .-target .-value) idx]))
                                                ;:on-change   #(rf/dispatch [:settings/edit-script (-> % .-target .-value) idx])
                                                }]]
                                  [:span.glyphicon.glyphicon-remove {:on-click #(do
                                                                                  (.preventDefault %)
                                                                                  (rf/dispatch [:settings/remove-script script]))}]]]
                                ])]
                            )}))


(defn javascript-tab []
  [:div.javascript-tab.content
   [:p.section-label "Scripts"
    [:span.question-small {:title "Add any script, drag to change the order, click to edit the path."}]]
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
       [:a.question-small {:href   "https://docs.anychart.com/Quick_Start/Modules"
                           :target "_blank"}]]
      [:div.line
       [:select.form-control {:id        "settings-select-bin"
                              :on-change #(rf/dispatch [:settings.external-resources/binaries-select (-> % .-target .-value)])}
        (for [res @(rf/subscribe [:settings.external-resources/binaries-groups])]
          ^{:key (:name res)}
          [:optgroup {:label (:name res)}
           (for [item (:items res)]
             ^{:key item} [:option {:value (:url item)} (:name item)])])
        ]
       (if @(rf/subscribe [:settings.external-resources/added-js? :binary])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :binary])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :binary])} "Add"])]]
     ]


    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-theme"} "AnyChart Themes"
       [:a.question-small {:href   "https://docs.anychart.com/Appearance_Settings/Themes"
                           :target "_blank"}]]
      [:div.line
       [:select.form-control {:id        "settings-select-theme"
                              :on-change #(rf/dispatch [:settings.external-resources/themes-select (-> % .-target .-value)])}
        (for [res @(rf/subscribe [:settings.external-resources/themes])]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :theme])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :theme])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :theme])} "Add"])]]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-locale"} "AnyChart Locales"
       [:a.question-small {:href   "https://docs.anychart.com/Common_Settings/Localization"
                           :target "_blank"}]]
      [:div.line
       [:select.form-control {:id        "settings-select-locale"
                              :on-change #(rf/dispatch [:settings.external-resources/locales-select (-> % .-target .-value)])}
        (for [res @(rf/subscribe [:settings.external-resources/locales])]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :locale])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :locale])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :locale])} "Add"])]]
     ]

    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-map"} "AnyChart Geo Data"
       [:a.question-small {:href   "https://docs.anychart.com/Maps/Maps_List"
                           :target "_blank"}]]
      [:div.line
       [:select.form-control {:id        "settings-select-map"
                              :on-change #(rf/dispatch [:settings.external-resources/maps-select (-> % .-target .-value)])}
        ;(for [res external-resources/maps]
        ;  ^{:key res} [:option {:value (:url res)} (:name res)])
        (for [res @(rf/subscribe [:settings.external-resources/maps-groups])]
          ^{:key (:name res)}
          [:optgroup {:label (:name res)}
           (for [item (:items res)]
             ^{:key item} [:option {:value (:url item)} (:name item)])])]
       (if @(rf/subscribe [:settings.external-resources/added-js? :map])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-js-by-type :map])} "Remove"]
         [:button.ac-btn.add-btn. {:type     "button"
                                   :on-click #(rf/dispatch [:settings.external-resources/add-js-by-type :map])} "Add"])]]
     ]]])