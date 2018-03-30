(ns playground.search.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :search/show (fn [db _] (-> db :search :show)))

(rf/reg-sub :search/results (fn [db _] (-> db :search :results)))