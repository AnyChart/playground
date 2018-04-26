(ns playground.settings-window.general-tab.events
  (:require [re-frame.core :as rf]
            [playground.data.tags :as tags-data]
            [playground.utils.utils :as common-utils]))


;;======================================================================================================================
;; Sample settings
;;======================================================================================================================
(rf/reg-event-db
  :settings/change-name
  (fn [db [_ name]]
    (assoc-in db [:sample :name] name)))


(rf/reg-event-db
  :settings/change-short-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :short-description] (common-utils/strip-tags value))))


(rf/reg-event-db
  :settings/change-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :description] (common-utils/strip-tags value))))


(rf/reg-event-db
  :settings/refresh-tags
  (fn [db _]
    (let [tags-by-code (tags-data/get-tags-by-code (-> db :sample :code))
          deleted-tags (-> db :sample :deleted-tags)
          new-tags (distinct (concat (-> db :sample :tags)
                                     (vec (clojure.set/difference
                                            (set tags-by-code)
                                            (set deleted-tags)))))
          ;; to set :settings :general-tab :tags
          settings-tags (-> db :settings :general-tab :tags)
          new-settings-tags (map (fn [tag-name]
                                   {:name     tag-name
                                    :selected (boolean (:selected (first (filter #(= tag-name (:name %)) settings-tags))))})
                                 new-tags)]
      (-> db
          (assoc-in [:sample :tags] new-tags)
          (assoc-in [:settings :general-tab :tags] new-settings-tags)))))


;;======================================================================================================================
;; General tabs: tags
;;======================================================================================================================
(rf/reg-event-db
  :settings/select-tag
  (fn [db [_ tag-name]]
    (-> db
        (update-in [:settings :general-tab :tags]
                   (fn [tags]
                     (map (fn [tag]
                            (if (= (:name tag) tag-name)
                              (update tag :selected not)
                              tag)) tags))))))


(rf/reg-event-fx
  :settings/tags-backspace
  (fn [{db :db} _]
    (let [last-tag (-> db :settings :general-tab :tags last)]
      (if-not (:selected last-tag)
        {:dispatch [:settings/select-tag (:name last-tag)]}
        {:dispatch [:settings/remove-tag (:name last-tag)]}))))


(rf/reg-event-db
  :settings/remove-tag
  (fn [db [_ value]]
    (-> db
        (update-in [:sample :tags] #(remove (partial = value) %))
        (update-in [:settings :general-tab :tags] #(remove (fn [tag] (= value (:name tag))) %))
        ;; add to deleted tags
        (update-in [:sample :deleted-tags] (fn [del-tags]
                                             (if (tags-data/anychart-tag? value)
                                               (distinct (conj del-tags value))
                                               del-tags))))))


(rf/reg-event-db
  :settings/add-tag
  (fn [db [_ value]]
    (if (every? (partial not= value) (-> db :sample :tags))
      (-> db
          (update-in [:sample :tags] concat [value])
          (update-in [:settings :general-tab :tags] concat [{:name value :selected false}])
          (update-in [:sample :deleted-tags] (fn [del-tags]
                                               (remove (partial = value) del-tags))))
      db)))
