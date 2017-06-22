(ns playground.settings-window.events
  (:require [re-frame.core :as rf]
            [clojure.string :as string]))

;;======================================================================================================================
;; Settings
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (assoc-in db [:settings :show] true)))

(rf/reg-event-db
  :settings/hide
  (fn [db _]
    (assoc-in db [:settings :show] false)))

(rf/reg-event-db
  :settings/general-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :general)))

(rf/reg-event-db
  :settings/external-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :external)))

(rf/reg-event-db
  :settings/data-sets-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :data-sets)))

(rf/reg-event-db
  :settings/change-name
  (fn [db [_ name]]
    (assoc-in db [:sample :name] name)))

(rf/reg-event-db
  :settings/change-short-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :short-description] value)))

(rf/reg-event-db
  :settings/change-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :description] value)))

(rf/reg-event-db
  :settings/add-script
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :scripts))
      (-> db
          (update-in [:sample :scripts] #(concat % [value]))
          (update-in [:settings :scripts-str] str (str "\n" value)))
      db)))

(rf/reg-event-db
  :settings/change-scripts
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :scripts] (filter seq (map string/trim (string/split-lines value))))
        (assoc-in [:settings :scripts-str] value))))

(rf/reg-event-db
  :settings/change-styles
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :styles] (filter seq (map string/trim (string/split-lines value))))
        (assoc-in [:settings :styles-str] value))))

(rf/reg-event-db
  :settings/change-tags
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :tags] (filter seq (map string/trim (string/split value #"\s"))))
        (assoc-in [:settings :tags-str] value))))