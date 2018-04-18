(ns playground.settings-window.css-tab.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [playground.settings-window.external-resources.views :as version-select]))


(defn styles-box []
  (reagent/create-class
    {:component-did-mount #(let [el (.getElementById js/document "styles-box")]
                             (.create js/Sortable el (clj->js {:animation 150
                                                               :draggable ".script"
                                                               :handle    ".glyphicon-align-justify"
                                                               :onEnd     (fn [e]
                                                                            (rf/dispatch [:settings/update-styles-order
                                                                                          (.-oldIndex e) (.-newIndex e)]))})))
     :reagent-render      (fn []
                            [:div#styles-box.scripts-box
                             (for [[idx style] (map-indexed (fn [idx style] [idx style])
                                                            @(rf/subscribe [:sample/styles]))]
                               ^{:key (str style "-" idx)}
                               [:div.script
                                [:span.script-box
                                 [:span.glyphicon.glyphicon-align-justify]
                                 [:div.in-box
                                  [:div.input-height-box
                                   [:span.height-line style]
                                   [:input.url {:type          "text"
                                                :default-value style
                                                :on-blur       #(rf/dispatch [:settings/edit-style (-> % .-target .-value) idx])
                                                :on-key-down   #(when (= 13 (.-keyCode %))
                                                                  (.blur (-> % .-target))
                                                                  (rf/dispatch [:settings/edit-style (-> % .-target .-value) idx]))
                                                ;:on-change   #(rf/dispatch [:settings/edit-style (-> % .-target .-value) idx])
                                                }]]
                                  [:span.glyphicon.glyphicon-remove {:on-click #(do
                                                                                  (.preventDefault %)
                                                                                  (rf/dispatch [:settings/remove-style style]))}]]]
                                ])]
                            )}))


(defn css-tab []
  [:div.javascript-tab.css-tab.content

   [:p.section-label "Styles"
    [:span.question-small {:title "Add any CSS, drag to change the order, click to edit the path."}]]
   [styles-box]

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

   ;[:p.section-label.quick-add "Quick Add"]
   [:p.section-label.quick-add ""]
   [version-select/version-select]

   [:div.row
    [:div.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-bin"} "AnyChart CSS"
       [:a.question-small {:href   "https://docs.anychart.com/Common_Settings/UI_Controls/AnyChart_UI"
                           :target "_blank"}]]
      [:div.line
       [:select.form-control {:id            "settings-select-bin"
                              :disabled      @(rf/subscribe [:settings.external-resources/loading])
                              :default-value @(rf/subscribe [:settings.external-resources/selected-resource :css])
                              :on-change     #(rf/dispatch [:settings.external-resources/css-select (-> % .-target .-value)])}
        (for [res @(rf/subscribe [:settings.external-resources/css])]
          ^{:key res} [:option {:value (:url res)} (:name res)])]
       (if @(rf/subscribe [:settings.external-resources/added-css? :css])
         [:button.ac-btn.remove-btn {:type     "button"
                                     :disabled @(rf/subscribe [:settings.external-resources/loading])
                                     :on-click #(rf/dispatch [:settings.external-resources/remove-css-by-type :css])} "Remove"]
         [:button.ac-btn.add-btn {:type     "button"
                                  :disabled @(rf/subscribe [:settings.external-resources/loading])
                                  :on-click #(rf/dispatch [:settings.external-resources/add-css-by-type :css])} "Add"])]]]]])
