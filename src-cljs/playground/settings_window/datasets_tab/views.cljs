(ns playground.settings-window.datasets-tab.views
  (:require [re-frame.core :as rf]))


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