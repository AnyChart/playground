(ns playground.embed-window.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :embed/show (fn [db _] (-> db :embed :show)))
(rf/reg-sub :embed/embed-tab? (fn [db _] (= :embed (-> db :embed :tab))))
(rf/reg-sub :embed/download-tab? (fn [db _] (= :download (-> db :embed :tab))))

(rf/reg-sub
  :embed/iframe
  (fn [query_v _] (rf/subscribe [:sample-iframe-url]))
  (fn [sample-iframe-url _]
    (when (seq sample-iframe-url)
      (str
        "<iframe sandbox=\"allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms\" allowtransparency=\"true\" allowfullscreen=\"true\" src=\""
        "http://pg.anychart.stg" sample-iframe-url "\" style=\"width:100%;height:100%;border:none;\"></iframe>"))))

(rf/reg-sub
  :embed/download-html-link
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _]
    (str "http://localhost:8085" sample-url "?view=download")))