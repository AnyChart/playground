(ns playground.tips.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :tips/current (fn [db _] (-> db :tips :current)))