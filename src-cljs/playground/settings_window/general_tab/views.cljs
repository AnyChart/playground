(ns playground.settings-window.general-tab.views
  (:require [re-frame.core :as rf]))


(defn general-tab []
  [:div.general-tab.content
   [:div.form-group
    [:label {:for "settings-name"} "Name"]
    [:input.form-control {:id            "settings-name"
                          :default-value @(rf/subscribe [:sample/name])
                          :on-change     #(rf/dispatch [:settings/change-name (-> % .-target .-value)])}]]
   [:div.form-group
    [:label {:for "settings-short-desc"} "Short Description"]
    [:textarea.form-control {:id        "settings-short-desc"
                             :value     @(rf/subscribe [:sample/stripped-short-description])
                             :on-change #(rf/dispatch [:settings/change-short-desc (-> % .-target .-value)])}]]
   [:div.form-group
    [:label {:for "settings-desc"} "Description"]
    (if @(rf/subscribe [:user-sample?])
      [:textarea.form-control {:id        "settings-desc"
                               :style     {:max-height @(rf/subscribe [:settings.general-tab/description-height])}
                               :value     @(rf/subscribe [:sample/description])
                               :on-change #(rf/dispatch [:settings/change-desc (-> % .-target .-value)])}]
      [:div {:dangerouslySetInnerHTML {:__html @(rf/subscribe [:sample/description])}}])]
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
        [:i.fas.fa-times.icon-close   ;:span.glyphicon.glyphicon-remove
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