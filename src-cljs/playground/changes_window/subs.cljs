(ns playground.changes-window.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :changes-window/show (fn [db _] (-> db :changes-window :show)))

(rf/reg-sub :changes-window/changes (fn [db _] (-> db :changes-window :changes)))