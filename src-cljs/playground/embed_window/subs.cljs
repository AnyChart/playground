(ns playground.embed-window.subs
  (:require [re-frame.core :as rf]))

;;======================================================================================================================
;; Main
;;======================================================================================================================
(rf/reg-sub :embed/show (fn [db _] (-> db :embed :show)))
(rf/reg-sub :embed/embed-tab? (fn [db _] (= :embed (-> db :embed :tab))))
(rf/reg-sub :embed/download-tab? (fn [db _] (= :download (-> db :embed :tab))))

;;======================================================================================================================
;; Download tab
;;======================================================================================================================
(rf/reg-sub
  :embed/download-html-link
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _]
    (str sample-url "?view=download")))

;;======================================================================================================================
;; Embed tab properties
;;======================================================================================================================
(rf/reg-sub :embed.props/id (fn [db _] (-> db :embed :props :id)))
(rf/reg-sub :embed.props/class (fn [db _] (-> db :embed :props :class)))
(rf/reg-sub :embed.props/width (fn [db _] (-> db :embed :props :width)))
(rf/reg-sub :embed.props/height (fn [db _] (-> db :embed :props :height)))