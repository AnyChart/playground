(ns playground.embed-window.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :embed/show (fn [db _] (-> db :embed :show)))
(rf/reg-sub :embed/embed-tab? (fn [db _] (= :embed (-> db :embed :tab))))
(rf/reg-sub :embed/download-tab? (fn [db _] (= :download (-> db :embed :tab))))

(rf/reg-sub
  :embed/download-html-link
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _]
    (str sample-url "?view=download")))