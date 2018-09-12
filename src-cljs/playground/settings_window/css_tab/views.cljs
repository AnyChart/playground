(ns playground.settings-window.css-tab.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [playground.settings-window.external-resources.views :as version-select]
            [playground.data.consts :as consts]))


(defn styles-box []
  (reagent/create-class
    {:component-did-mount #(let [el (.getElementById js/document "styles-box")]
                             (.create js/Sortable el (clj->js {:animation 150
                                                               :draggable ".script"
                                                               :handle    ".icon-move"
                                                               :onEnd     (fn [e]
                                                                            (rf/dispatch [:settings/update-styles-order
                                                                                          (.-oldIndex e) (.-newIndex e)]))})))
     :reagent-render      (fn []
                            [:div#styles-box.scripts-box
                             (for [[idx {style :style warning :warning}] (map-indexed (fn [idx style] [idx style])
                                                                                      @(rf/subscribe [:settings.css-tab/correct-styles]))]
                               ^{:key (str style "-" idx)}
                               [:div.script
                                [:span.script-box
                                 [:i.fas.fa-bars.icon-move]
                                 [:div.in-box
                                  [:div.input-height-box {:title warning}
                                   [:span.height-line style]
                                   [:input.url {:type          "text"
                                                :default-value style
                                                :on-blur       #(rf/dispatch [:settings/edit-style (-> % .-target .-value) idx])
                                                :on-key-down   #(when (= 13 (.-keyCode %))
                                                                  (.blur (-> % .-target))
                                                                  (rf/dispatch [:settings/edit-style (-> % .-target .-value) idx]))
                                                ;:on-change   #(rf/dispatch [:settings/edit-style (-> % .-target .-value) idx])
                                                }]]
                                  [:i.fas.fa-times.icon-close {:on-click #(do
                                                                            (.preventDefault %)
                                                                            (rf/dispatch [:settings/remove-style style]))}]]
                                 (when warning
                                   [:span.glyphicon.glyphicon-warning-sign
                                    {:title warning}])
                                 ]
                                ])]
                            )}))


(defn css-tab []
  [:div.javascript-tab.css-tab.content

   [:p.section-label.tooltiped "Styles"
    [:span.question-small.tooltiped
     [:span.tooltip-box consts/styles-title]]]
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

   [:div                                                    ;.row
    [:div                                                   ;.col-sm-6
     [:div.form-group
      [:label {:for "settings-select-bin"} "AnyChart CSS"
       [:span.question-small.tooltiped
        [:span.tooltip-box consts/anychart-css
         [:a {:href   "https://docs.anychart.com/Quick_Start/Modules"
              :target "_blank"} "Read more >>"]]]]
      (let [loading @(rf/subscribe [:settings.external-resources/loading])
            csss @(rf/subscribe [:settings.external-resources/css])
            disabled (or loading (empty? csss))]
        [:div.line
         [:select.form-control {:id        "settings-select-bin"
                                :disabled  disabled
                                :value     @(rf/subscribe [:settings.external-resources/selected-resource :css])
                                :on-change #(rf/dispatch [:settings.external-resources/css-select (-> % .-target .-value)])}
          (for [res csss]
            ^{:key res} [:option {:value (:url res)} (:name res)])]
         (if @(rf/subscribe [:settings.external-resources/added-css? :css])
           [:button.ac-btn.remove-btn {:type     "button"
                                       :disabled disabled
                                       :on-click #(rf/dispatch [:settings.external-resources/remove-css-by-type :css])} "Remove"]
           [:button.ac-btn.add-btn {:type     "button"
                                    :disabled disabled
                                    :on-click #(rf/dispatch [:settings.external-resources/add-css-by-type :css])} "Add"])])]]]])
