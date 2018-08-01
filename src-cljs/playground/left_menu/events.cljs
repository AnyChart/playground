(ns playground.left-menu.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  :left-menu/show
  (fn [db _]
    (assoc-in db [:left-menu :show] true)))


(rf/reg-event-db
  :left-menu/close
  (fn [db _]
    (assoc-in db [:left-menu :show] false)))


(rf/reg-event-db
  :left-menu/toggle
  (fn [db _]
    (update-in db [:left-menu :show] not)))