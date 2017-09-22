(ns playground.editors.views
  (:require [re-com.core :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-args-desc]]
            [playground.utils :as utils]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [playground.settings-window.data :as external-resources]
            [playground.standalone.views :as standalone-view]))

(defn iframe-result []
  [:div.result
   [:iframe#result-iframe {:name              "result-iframe"
                           :class             "iframe-result"
                           :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                           :allowTransparency "true"
                           :allowFullScreen   "true"
                           :src               @(rf/subscribe [:sample-iframe-url])}]])


(defn markup-editor []
  [:div.editor-container
   ;[:a.editor-label.editor-label-gear {:on-click #(utils/log "JS settings click!")}
   ; [:span "javascript"]
   ; [:span.glyphicon.glyphicon-cog {:aria-hidden true}]]
   [:a.editor-label.editor-label-copy {:id "markup-editor-copy"}
    [:span "copy"]
    [:div.icon.icon-copy]]
   [:div#markup-editor {:class "editor-box"}]])


(defn code-editor []
  [:div.editor-container
   [:a.editor-label.editor-label-gear {:id       "code-editor-settings-button"
                                       :on-click #(rf/dispatch [:editors.code-settings/show])}
    [:span "javascript"]
    [:div.icon.icon-settings]]
   [:a.editor-label.editor-label-copy {:id "code-editor-copy"}
    [:span "copy"]
    [:div.icon.icon-copy]]
   (when @(rf/subscribe [:editors.code-settings/show])
     [:div.code-context-menu {:id "code-context-menu"}
      [:h4 "Added resources"]
      [:div
       (for [res @(rf/subscribe [:editors/external-resources])]
         ^{:key (:name res)} [:div.settings-resource
                              [:a.title {:href   (:url res)
                                         :target "_blank"} (:name res)]
                              ;[:button.btn.btn-primary.btn-xs {:type     "button"
                              ;                                 :on-click #(rf/dispatch [:settings/remove-script (:url res)])}
                              ; [:span.glyphicon.glyphicon-remove]]
                              [:span.glyphicon.glyphicon-remove.code-context-menu-close-icon
                               {:on-click #(rf/dispatch [:settings/remove-script (:url res)])}]
                              ])
       [:h4 "External recources"]
       [:select {:on-change #(rf/dispatch [:settings/add-script (-> % .-target .-value)])}
        [:optgroup {:label "Binaries"}
         (for [res external-resources/binaries]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Themes"}
         (for [res external-resources/themes]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Locales"}
         (for [res external-resources/locales]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]
        [:optgroup {:label "Maps"}
         (for [res external-resources/maps]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]]
       ]
      ])
   [:div#code-editor {:class "editor-box"}]])


(defn style-editor []
  [:div.editor-container
   ;[:a.editor-label.editor-label-gear {:on-click #(utils/log "JS settings click!")}
   ; [:span "javascript"]
   ; [:span.glyphicon.glyphicon-cog {:aria-hidden true}]]
   [:a.editor-label.editor-label-copy {:id "style-editor-copy"}
    [:span "copy"]
    [:div.icon.icon-copy]]
   [:div#style-editor {:class "editor-box"}]])


(defn editors-left []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [h-split
                                                   :class "splitter"
                                                   :splitter-size "8px"
                                                   :panel-1 [v-split
                                                             :margin "0px"
                                                             :splitter-size "8px"
                                                             :initial-split markup-percent
                                                             :panel-1 [markup-editor]
                                                             :panel-2 [v-split
                                                                       :margin "0px"
                                                                       :splitter-size "8px"
                                                                       :initial-split style-percent
                                                                       :panel-1 [style-editor]
                                                                       :panel-2 [code-editor]]]
                                                   :panel-2 [iframe-result]]))}))

(defn editors-right []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [h-split
                                                   :class "splitter"
                                                   :splitter-size "8px"
                                                   :panel-2 [v-split
                                                             :margin "0px"
                                                             :splitter-size "8px"
                                                             :initial-split markup-percent
                                                             :panel-1 [markup-editor]
                                                             :panel-2 [v-split
                                                                       :margin "0px"
                                                                       :splitter-size "8px"
                                                                       :initial-split style-percent
                                                                       :panel-1 [style-editor]
                                                                       :panel-2 [code-editor]]]
                                                   :panel-1 [iframe-result]]))}))

(defn editors-top []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                [v-split
                                                 :class "splitter"
                                                 :splitter-size "8px"
                                                 :panel-1 [h-split
                                                           :margin "0px"
                                                           :splitter-size "8px"
                                                           :initial-split 33
                                                           :panel-1 [markup-editor]
                                                           :panel-2 [h-split
                                                                     :margin "0px"
                                                                     :splitter-size "8px"
                                                                     :panel-1 [style-editor]
                                                                     :panel-2 [code-editor]]]
                                                 :panel-2 [iframe-result]])}))

(defn editors-bottom []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                [v-split
                                                 :class "splitter"
                                                 :splitter-size "8px"
                                                 :panel-2 [h-split
                                                           :margin "0px"
                                                           :splitter-size "8px"
                                                           :initial-split 33
                                                           :panel-1 [markup-editor]
                                                           :panel-2 [h-split
                                                                     :margin "0px"
                                                                     :splitter-size "8px"
                                                                     :panel-1 [style-editor]
                                                                     :panel-2 [code-editor]]]
                                                 :panel-1 [iframe-result]])}))

(defn editors []
  [:div.column-container {:style {:height @(rf/subscribe [:editors/height])}}
   (case @(rf/subscribe [:editors/view])
     :left [editors-left]
     :right [editors-right]
     :top [editors-top]
     :bottom [editors-bottom]
     :standalone [standalone-view/view])])