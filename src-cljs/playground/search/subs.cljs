(ns playground.search.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :search/show (fn [db _] (-> db :search :show)))

;(rf/reg-sub :search/results (fn [db _] (-> db :search :results)))

(rf/reg-sub :search/query-hints (fn [db _] (-> db :search :query-hints)))

(rf/reg-sub :search/query (fn [db _] (-> db :search :query)))