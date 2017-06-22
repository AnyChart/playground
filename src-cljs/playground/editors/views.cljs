(ns playground.editors.views
  (:require [re-com.core :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-args-desc]]
            [playground.utils :as utils]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn iframe-result []
  [:div.result
   [:iframe#result-iframe {:name              "result-iframe"
                           :class             "iframe-result"
                           :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                           :allowTransparency "true"
                           :allowFullScreen   "true"
                           :src               @(rf/subscribe [:sample-iframe-url])}]])


(defn editors-left []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [h-split
                                                   :class "cont1"
                                                   :splitter-size "8px"
                                                   :panel-1 [v-split
                                                             :margin "0px"
                                                             :splitter-size "8px"
                                                             :initial-split markup-percent
                                                             :panel-1 [:div#markup-editor {:class "editor-box"}]
                                                             :panel-2 [v-split
                                                                       :margin "0px"
                                                                       :splitter-size "8px"
                                                                       :initial-split style-percent
                                                                       :panel-1 [:div#style-editor {:class "editor-box"}]
                                                                       :panel-2 [:div#code-editor {:class "editor-box"}]]]
                                                   :panel-2 [iframe-result]]))}))

(defn editors-right []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                (let [[markup-percent style-percent] @(rf/subscribe [:editors/splitter-percents])]
                                                  [h-split
                                                   :class "cont1"
                                                   :splitter-size "8px"
                                                   :panel-2 [v-split
                                                             :margin "0px"
                                                             :splitter-size "8px"
                                                             :initial-split markup-percent
                                                             :panel-1 [:div#markup-editor {:class "editor-box"}]
                                                             :panel-2 [v-split
                                                                       :margin "0px"
                                                                       :splitter-size "8px"
                                                                       :initial-split style-percent
                                                                       :panel-1 [:div#style-editor {:class "editor-box"}]
                                                                       :panel-2 [:div#code-editor {:class "editor-box"}]]]
                                                   :panel-1 [iframe-result]]))}))

(defn editors-top []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                [v-split
                                                 :class "cont1"
                                                 :splitter-size "8px"
                                                 :panel-1 [h-split
                                                           :margin "0px"
                                                           :splitter-size "8px"
                                                           :initial-split 33
                                                           :panel-1 [:div#markup-editor {:class "editor-box"}]
                                                           :panel-2 [h-split
                                                                     :margin "0px"
                                                                     :splitter-size "8px"
                                                                     :panel-1 [:div#style-editor {:class "editor-box"}]
                                                                     :panel-2 [:div#code-editor {:class "editor-box"}]]]
                                                 :panel-2 [iframe-result]])}))

(defn editors-bottom []
  (reagent/create-class {:component-did-mount #(do (utils/log "Did mount!") (rf/dispatch [:create-editors]))
                         :reagent-render      (fn []
                                                [v-split
                                                 :class "cont1"
                                                 :splitter-size "8px"
                                                 :panel-2 [h-split
                                                           :margin "0px"
                                                           :splitter-size "8px"
                                                           :initial-split 33
                                                           :panel-1 [:div#markup-editor {:class "editor-box"}]
                                                           :panel-2 [h-split
                                                                     :margin "0px"
                                                                     :splitter-size "8px"
                                                                     :panel-1 [:div#style-editor {:class "editor-box"}]
                                                                     :panel-2 [:div#code-editor {:class "editor-box"}]]]
                                                 :panel-1 [iframe-result]])}))

(defn editors []
  [:div.column-container {:style {:height @(rf/subscribe [:editors/height])}}
   (case @(rf/subscribe [:editors/view])
     :left [editors-left]
     :right [editors-right]
     :top [editors-top]
     :bottom [editors-bottom])])