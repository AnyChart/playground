(ns playground.settings-window.css-tab.events
  (:require [re-frame.core :as rf]
            [playground.utils.utils :as common-utils]
            [playground.settings-window.javascript-tab.events :refer [detect-version-interceptor]]))


;;======================================================================================================================
;; Add/remove css
;;======================================================================================================================
(rf/reg-event-db
  :settings/add-style
  [detect-version-interceptor]
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :styles))
      (-> db
          (update-in [:sample :styles] #(concat % [value]))
          (update-in [:tips :queue] conj value))
      db)))


(rf/reg-event-db
  :settings/remove-style
  [detect-version-interceptor]
  (fn [db [_ value]]
    (update-in db [:sample :styles] (fn [styles] (remove #(= value %) styles)))))


(rf/reg-event-db
  :settings.external-resources/add-css-by-type
  [detect-version-interceptor]
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :styles] #(concat % [url]))
          (update-in [:tips :queue] conj url)))))


(rf/reg-event-db
  :settings.external-resources/remove-css-by-type
  [detect-version-interceptor]
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :styles] (fn [scripts] (remove #(= url %) scripts)))
          (update-in [:tips :queue] (fn [tips-urls] (remove #(= url %) tips-urls)))))))


(rf/reg-event-db
  :settings/edit-style
  [detect-version-interceptor]
  (fn [db [_ val index]]
    (-> db
        (update-in [:sample :styles] (fn [styles]
                                       (let [styles (vec styles)]
                                         (assoc styles index val)))))))


(rf/reg-event-db
  :settings/update-styles-order
  [detect-version-interceptor]
  (fn [db [_ old-index new-index]]
    (-> db
        (update-in [:sample :styles] #(common-utils/reorder-list % old-index new-index)))))
