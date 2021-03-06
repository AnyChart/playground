(ns playground.changes-window.views
  (:require [re-frame.core :as rf]))


(defn changes-window-collapsed []
  [:div.changes-window.collapsed {:on-click #(rf/dispatch [:changes-window/expand])}
   [:div.title "Changes not saved"
    [:i.fas.fa-exclamation-triangle.icon-warning]]])


(defn changes-window-expand []
  [:div.changes-window

   [:div.title "Changes not saved"
    [:i.fas.fa-exclamation-triangle.icon-warning]]

   [:div.text "There are unsaved local changes, that will be lost if you close the window."]

   [:div "Changes:"]
   [:ul
    (for [change @(rf/subscribe [:changes-window/changes])]
      ^{:key change} [:li change])]

   [:a.apply-button {:on-click #(rf/dispatch [:changes-window/apply-changes])}
    [:i.fas.fa-check.icon-ok] "Apply Changes"]

   ;[:button.ac-btn.add-btn "Apply Changes"]

   [:a.discard-button {:on-click #(rf/dispatch [:changes-window/discard-changes])}
    [:i.fas.fa-times.icon-close] "Discard Changes"]

   ;[:button.ac-btn.remove-btn "Discard Changes"]
   ])


(defn changes-window []
  (when @(rf/subscribe [:changes-window/show])
    (if @(rf/subscribe [:changes-window/expand])
      [changes-window-expand]
      [changes-window-collapsed])))