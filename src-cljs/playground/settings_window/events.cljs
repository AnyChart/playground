(ns playground.settings-window.events
  (:require [re-frame.core :as rf]
            [playground.modal-window.events :refer [show-modal-warning]]))

;;======================================================================================================================
;; Settings window
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (assoc-in db [:settings :show] true)))


(rf/reg-event-db
  :settings/hide
  [show-modal-warning]
  (fn [db _]
    ;TODO: eliminate dispatch in event handler
    (rf/dispatch [:tips/add-from-queue])
    (assoc-in db [:settings :show] false)))


(rf/reg-event-db
  :settings/general-tab
  [show-modal-warning]
  (fn [db _]
    (assoc-in db [:settings :tab] :general)))


(rf/reg-event-db
  :settings/javascript-tab
  [show-modal-warning]
  (fn [db _]
    (assoc-in db [:settings :tab] :javascript)))


(rf/reg-event-db
  :settings/css-tab
  [show-modal-warning]
  (fn [db _]
    (assoc-in db [:settings :tab] :css)))


(rf/reg-event-db
  :settings/datasets-tab
  [show-modal-warning]
  (fn [db _]
    (assoc-in db [:settings :tab] :datasets)))
