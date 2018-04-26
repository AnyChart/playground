(ns playground.settings-window.events
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [playground.data.tags :as tags-data]
            [playground.utils.utils :as common-utils]
            [playground.settings-window.external-resources.parser :as external-resources-parser]))

;;======================================================================================================================
;; Settings window
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (assoc-in db [:settings :show] true)))


(rf/reg-event-db
  :settings/hide
  (fn [db _]
    ;TODO: eliminate dispatch in event handler
    (rf/dispatch [:tips/add-from-queue])
    (assoc-in db [:settings :show] false)))


(rf/reg-event-db
  :settings/general-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :general)))


(rf/reg-event-db
  :settings/javascript-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :javascript)))


(rf/reg-event-db
  :settings/css-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :css)))


(rf/reg-event-db
  :settings/datasets-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :datasets)))
