(ns playground.modal-window.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :modal/show (fn [db _] (-> db :modal :show)))