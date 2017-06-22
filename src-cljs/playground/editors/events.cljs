(ns playground.editors.events
  (:require [re-frame.core :as rf]
            [playground.editors.js :as editors-js]))

;;======================================================================================================================
;; Editors
;;======================================================================================================================
(rf/reg-event-db
  :create-editors
  (fn [db _]
    (assoc db
      :markup-editor (editors-js/create-editor :markup (-> db :sample :markup) "text/html")
      :style-editor (editors-js/create-editor :style (-> db :sample :style) "css")
      :code-editor (editors-js/create-editor :code (-> db :sample :code) "javascript"))))

(rf/reg-event-db
  :resize-window
  (fn [db _]
    (assoc db :editors-height (editors-js/editors-height))))

(rf/reg-event-db
  :view/left
  (fn [db _]
    (assoc-in db [:view] :left)))

(rf/reg-event-db
  :view/right
  (fn [db _]
    (assoc-in db [:view] :right)))

(rf/reg-event-db
  :view/bottom
  (fn [db _]
    (assoc-in db [:view] :bottom)))

(rf/reg-event-db
  :view/top
  (fn [db _]
    (assoc-in db [:view] :top)))