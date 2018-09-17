(ns playground.changes-window.views
  (:require [re-frame.core :as rf]))


(defn changes-window []
  (when @(rf/subscribe [:changes-window/show])
    [:div.changes-window

     [:div.title "Changes not saved"
      [:i.fas.fa-exclamation-triangle.icon-warning]]
     [:div.text "There are unsaved local changes, that will be lost if you close the window."]

     [:div "Changes:"]
     [:ul
      (for [change @(rf/subscribe [:changes-window/changes])]
        [:li change]
        )]

     [:a.apply-button {:on-click #(rf/dispatch [:changes-window/apply-changes])}
      [:i.fas.fa-check.icon-ok] "Apply changes"]

     [:a.discard-button {:on-click #(rf/dispatch [:changes-window/discard-changes])}
      [:i.fas.fa-times.icon-close] "Discard changes"]

     ]))