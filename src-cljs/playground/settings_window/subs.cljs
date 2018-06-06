(ns playground.settings-window.subs
  (:require [re-frame.core :as rf]))


;; Settings Tabs
(rf/reg-sub :settings/show (fn [db _] (-> db :settings :show)))

(rf/reg-sub :settings/general-tab? (fn [db _] (= :general (-> db :settings :tab))))

(rf/reg-sub :settings/javascript-tab? (fn [db _] (= :javascript (-> db :settings :tab))))

(rf/reg-sub :settings/css-tab? (fn [db _] (= :css (-> db :settings :tab))))

(rf/reg-sub :settings/datasets-tab? (fn [db _] (= :datasets (-> db :settings :tab))))


(rf/reg-sub
  :settings.general-tab/description-height
  (fn [query_v _] (rf/subscribe [:editors/height]))
  (fn [editor-height _]
    (let [;y (.-y (.getBoundingClientRect (.getElementById js/document "settings-desc")))
          max-editor-height (- editor-height 200)]
      (if (< max-editor-height 80)
        80
        max-editor-height))))


(rf/reg-sub :settings/tags (fn [db _] (-> db :settings :general-tab :tags)))


(rf/reg-sub :settings/versions-names (fn [db _] (concat ["latest" "Release Candidate"] (-> db :versions-names))))


(rf/reg-sub
  :settings/correct-scripts-styles
  (fn [_ _] [(rf/subscribe [:settings.javascript-tab/correct-tab])
             (rf/subscribe [:settings.css-tab/correct-tab])])
  (fn [[is-correct-scripts is-correct-styles] _]
    (and is-correct-scripts is-correct-styles)))
