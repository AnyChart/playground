(ns playground.left-panel.views
  (:require [re-frame.core :as rf]
            [playground.utils.utils :as utils]))


(defn general-tab []
  [:div.general-tab
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
        [:i.fas.fa-times.icon-close                         ;:span.glyphicon.glyphicon-remove
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
   ])


(defn documentation-tab []
  (let [{:keys [articles-docs articles-api articles-pg]} @(rf/subscribe [:left-panel/docs])]
    [:div.docs-tab-container
     [:div.docs-tab
      (for [link articles-docs]
        ^{:key (:url link)} [:div.link.link-docs
                             [:a {:href   (str "https://docs.anychart.com/" (:url link))
                                  :target "_blank"
                                  :title  (:title link)}
                              (:title link)]])

      (for [link articles-pg]
        ^{:key (:url link)} [:div.link.link-docs
                             [:a {:href   (str "https://docs.anychart.com/" (:url link))
                                  :target "_blank"
                                  :title  (:title link)}
                              (:title link)]])

      (for [link articles-api]
        ^{:key (:url link)} [:div.link
                             [:a {:href   (str "https://docs.anychart.com/" (:url link))
                                  :target "_blank"
                                  :title  (:title link)}
                              (:title link)]])
      ]]))


(defn inner-footer []
  [:div.footer-inner
   [:a.soc-network
    {:title    "AnyChart Facebook"
     :target   "_blank"
     :rel      "nofollow"
     :on-click #(.stopPropagation %)
     :href     "https://www.facebook.com/AnyCharts"}
    [:span.soc-network-icon.fb [:i.sn-mini-icon.ac.ac-facebook]]]
   [:a.soc-network
    {:title    "AnyChart Twitter"
     :target   "_blank"
     :rel      "nofollow"
     :on-click #(.stopPropagation %)
     :href     "https://twitter.com/AnyChart"}
    [:span.soc-network-icon.tw [:i.sn-mini-icon.ac.ac-twitter]]]
   [:a.soc-network
    {:title    "AnyChart LinkedIn"
     :target   "_blank"
     :rel      "nofollow"
     :on-click #(.stopPropagation %)
     :href     "https://www.linkedin.com/company/386660"}
    [:span.soc-network-icon.in [:i.sn-mini-icon.ac.ac-linkedin]]]])


(defn bottom-footer []
  [:div.footer-bottom-box
   [inner-footer]
   [:div.copyright (str "\u00A9 " (utils/year) " ")
    [:a {:href   "https://www.anychart.com"
         :rel    "nofollow"
         :target "_blank"} "AnyChart.com"]
    " All rights reserved."]])


(defn left-panel-view []
  [:div.left-panel.general-tab.content {:style {;:height     @(rf/subscribe [:editors/height])
                                                :margin-top @(rf/subscribe [:editors/margin-top])}}

   [:div.collapse-button {:title    "Collapse panel"
                          :on-click #(rf/dispatch [:left-panel/collapse])}
    [:i.fas.fa-arrow-left]]

   (let [show-general @(rf/subscribe [:left-panel/general-tab?])]
     (list
       ^{:key "button"} [:div.accordion {:class    (when show-general "active")
                                         :on-click #(rf/dispatch [:left-panel/show-general])}
                         "Sample meta"]
       (when show-general
         ^{:key "tab"} [general-tab])))

   (let [show-docs @(rf/subscribe [:left-panel/docs-tab?])]
     (list
       ^{:key "button"} [:div.accordion {:class    (when show-docs "active")
                                         :on-click #(rf/dispatch [:left-panel/show-docs])}
                         "AnyChart Documentation"]
       (when show-docs
         ^{:key "tab"} [documentation-tab])))

   [bottom-footer]])


(defn left-panel-collapsed []
  [:div.left-panel-collapsed {:on-click #(rf/dispatch [:left-panel/expand])}
   [:div
    [:i.fas.fa-info-circle.icon-info]]
   [inner-footer]])


(defn view []
  (if @(rf/subscribe [:left-panel/collapsed])
    [left-panel-collapsed]
    [left-panel-view]))
