(ns playground.editors.views
  (:require [re-com.core :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-args-desc]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [playground.standalone.views :as standalone-view]))


(defn iframe []
  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:on-update-iframe]))
                         :reagent-render      (fn []
                                                [:iframe {:id                "result-iframe"
                                                          :name              "result-iframe"
                                                          :class             "iframe-result"
                                                          :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                                                          :allowTransparency "true"
                                                          :allowFullScreen   "true"}])}))


(defn iframe-result []
  [:div.result
   [:div.iframe-hider {:style {:display (if @(rf/subscribe [:editors/iframe-hider-show]) "block" "none")}}]
   (let [k @(rf/subscribe [:editors/iframe-update])]
     ^{:key (inc k)} [iframe])])


(defn markup-editor []
  [:div.editor-container
   ;[:a.editor-label.editor-label-gear {:on-click #(println "JS settings click!")}
   ; [:span "javascript"]
   ; [:span.glyphicon.glyphicon-cog {:aria-hidden true}]]
   [:div.top-line
    [:span.editor-label-name "HTML"]
    (when @(rf/subscribe [:editors/show-markup-copy-button])
      [:a#markup-editor-copy.editor-label.editor-label-copy
       [:span "copy"]
       [:div.icon.icon-copy]])]
   [:div#markup-editor.editor-box]])


(defn code-editor []
  [:div.editor-container
   [:div.top-line
    [:span.editor-label-name "JavaScript"]
    (when @(rf/subscribe [:editors/show-code-copy-button])
      [:a#code-editor-copy.editor-label.editor-label-copy
       [:span "copy"]
       [:div.icon.icon-copy]])
    ;; TODO: wait js button design
    ;[:a.editor-label.editor-label-gear {:id       "code-editor-settings-button"
    ;                                    :on-click #(rf/dispatch [:editors.code-settings/show])}
    ; [:span "javascript"]
    ; [:div.icon.icon-settings]]
    ]
   (when @(rf/subscribe [:editors.code-settings/show])
     [:div#code-context-menu.code-context-menu
      [:h4 "Added resources"]
      [:div
       (for [res @(rf/subscribe [:editors/external-resources])]
         ^{:key (:name res)} [:div.settings-resource
                              [:a.title {:href   (:url res)
                                         :target "_blank"} (:name res)]
                              [:span.glyphicon.glyphicon-remove.code-context-menu-close-icon
                               {:on-click #(rf/dispatch [:settings/remove-script (:url res)])}]])
       [:h4 "External recources"]
       [:select {:on-change #(rf/dispatch [:settings/add-script (-> % .-target .-value)])}

        ;; Binaries
        (for [res @(rf/subscribe [:settings.external-resources/binaries-groups])]
          ^{:key (:name res)}
          [:optgroup {:label (:name res)}
           (for [item (:items res)]
             ^{:key item} [:option {:value (:url item)} (:name item)])])

        ;; Themes
        [:optgroup {:label "Themes"}
         (for [res @(rf/subscribe [:settings.external-resources/themes])]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]

        ;; Locales
        [:optgroup {:label "Locales"}
         (for [res @(rf/subscribe [:settings.external-resources/locales])]
           ^{:key (:url res)} [:option {:value (:url res)} (:name res)])]

        ;; Geodata (maps)
        (for [res @(rf/subscribe [:settings.external-resources/maps-groups])]
          [:optgroup {:label (:name res)}
           (for [item (:items res)]
             ^{:key item} [:option {:value (:url item)} (:name item)])])
        ]]])
   [:div#code-editor.editor-box]])


(defn style-editor []
  [:div.editor-container
   ;[:a.editor-label.editor-label-gear {:on-click #(println "JS settings click!")}
   ; [:span "javascript"]
   ; [:span.glyphicon.glyphicon-cog {:aria-hidden true}]]
   [:div.top-line
    [:span.editor-label-name "CSS"]
    (when @(rf/subscribe [:editors/show-style-copy-button])
      [:a#style-editor-copy.editor-label.editor-label-copy
       [:span "copy"]
       [:div.icon.icon-copy]])]
   [:div#style-editor.editor-box]])


;(defn editors-left-old []
;  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:create-editors]))
;                         :reagent-render      (fn []
;                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
;                                                  [h-split
;                                                   :class "splitter"
;                                                   :splitter-size "8px"
;                                                   :panel-1 [v-split
;                                                             :margin "0px"
;                                                             :splitter-size "8px"
;                                                             :initial-split markup-percent
;                                                             :panel-1 [markup-editor]
;                                                             :panel-2 [v-split
;                                                                       :margin "0px"
;                                                                       :splitter-size "8px"
;                                                                       :initial-split style-percent
;                                                                       :panel-1 [style-editor]
;                                                                       :panel-2 [code-editor]]]
;                                                   :panel-2 [iframe-result]]))}))
;
;
;(defn editors-right-old []
;  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:create-editors]))
;                         :reagent-render      (fn []
;                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
;                                                  [h-split
;                                                   :class "splitter"
;                                                   :splitter-size "8px"
;                                                   :panel-2 [v-split
;                                                             :margin "0px"
;                                                             :splitter-size "8px"
;                                                             :initial-split markup-percent
;                                                             :panel-1 [markup-editor]
;                                                             :panel-2 [v-split
;                                                                       :margin "0px"
;                                                                       :splitter-size "8px"
;                                                                       :initial-split style-percent
;                                                                       :panel-1 [style-editor]
;                                                                       :panel-2 [code-editor]]]
;                                                   :panel-1 [iframe-result]]))}))
;
;(defn editors-top-old []
;  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:create-editors]))
;                         :reagent-render      (fn []
;                                                [v-split
;                                                 :class "splitter"
;                                                 :splitter-size "8px"
;                                                 :panel-1 [h-split
;                                                           :margin "0px"
;                                                           :splitter-size "8px"
;                                                           :initial-split 33
;                                                           :panel-1 [markup-editor]
;                                                           :panel-2 [h-split
;                                                                     :margin "0px"
;                                                                     :splitter-size "8px"
;                                                                     :panel-1 [style-editor]
;                                                                     :panel-2 [code-editor]]]
;                                                 :panel-2 [iframe-result]])}))
;
;(defn editors-bottom-old []
;  (reagent/create-class {:component-did-mount #(do (rf/dispatch [:create-editors]))
;                         :reagent-render      (fn []
;                                                [v-split
;                                                 :class "splitter"
;                                                 :splitter-size "8px"
;                                                 :panel-2 [h-split
;                                                           :margin "0px"
;                                                           :splitter-size "8px"
;                                                           :initial-split 33
;                                                           :panel-1 [markup-editor]
;                                                           :panel-2 [h-split
;                                                                     :margin "0px"
;                                                                     :splitter-size "8px"
;                                                                     :panel-1 [style-editor]
;                                                                     :panel-2 [code-editor]]]
;                                                 :panel-1 [iframe-result]])}))

(defn editors-left []
  (reagent/create-class {:component-did-mount #(do
                                                 (rf/dispatch [:create-editors])
                                                 (.init js/splitMe))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [:div.vertically_divided {:data-percent 50
                                                                            :style        {:width "100%" :height "100%"}}
                                                   [iframe-result]
                                                   [:div.horizontally_divided.z1 {:data-percent markup-percent}
                                                    [markup-editor]
                                                    [:div.horizontally_divided.z2 {:data-percent style-percent}
                                                     [style-editor]
                                                     [code-editor]]]]))}))


(defn editors-right []
  (reagent/create-class {:component-did-mount #(do
                                                 (rf/dispatch [:create-editors])
                                                 (.init js/splitMe))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [:div.vertically_divided {:data-percent 50
                                                                            :style        {:width "100%" :height "100%"}}
                                                   [:div.horizontally_divided.z1 {:data-percent markup-percent}
                                                    [markup-editor]
                                                    [:div.horizontally_divided.z2 {:data-percent style-percent}
                                                     [style-editor]
                                                     [code-editor]]]
                                                   [iframe-result]]))}))


(defn editors-top []
  (reagent/create-class {:component-did-mount #(do
                                                 (rf/dispatch [:create-editors])
                                                 (.init js/splitMe))
                         :reagent-render      (fn []
                                                [:div.horizontally_divided {:data-percent 50
                                                                            :style        {:width "100%" :height "100%"}}
                                                 [iframe-result]
                                                 [:div.vertically_divided.z1 {:data-percent 33}
                                                  [markup-editor]
                                                  [:div.vertically_divided.z2 {:data-percent 50}
                                                   [style-editor]
                                                   [code-editor]]]
                                                 ])}))


(defn editors-bottom []
  (reagent/create-class {:component-did-mount #(do
                                                 (rf/dispatch [:create-editors])
                                                 (.init js/splitMe))
                         :reagent-render      (fn []
                                                [:div.horizontally_divided {:data-percent 50
                                                                            :style        {:width "100%" :height "100%"}}
                                                 [:div.vertically_divided.z1 {:data-percent 33}
                                                  [markup-editor]
                                                  [:div.vertically_divided.z2 {:data-percent 50}
                                                   [style-editor]
                                                   [code-editor]]]
                                                 [iframe-result]])}))


(defn editors []
  [:div.column-container {:style {:height     @(rf/subscribe [:editors/height])
                                  :margin-top @(rf/subscribe [:editors/margin-top])}}
   (case @(rf/subscribe [:editors/view])
     :left [editors-left]
     :right [editors-right]
     :top [editors-top]
     :bottom [editors-bottom]
     :standalone [standalone-view/view])])