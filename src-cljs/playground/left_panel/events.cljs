(ns playground.left-panel.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  :left-panel/collapse
  (fn [db _]
    (assoc-in db [:left-panel :collapsed] true)))


(rf/reg-event-db
  :left-panel/expand
  (fn [db _]
    (assoc-in db [:left-panel :collapsed] false)))


(rf/reg-event-db
  :left-panel/show-general
  (fn [db _]
    (assoc-in db [:left-panel :tab] :general)))


(rf/reg-event-db
  :left-panel/show-docs
  (fn [db _]
    (assoc-in db [:left-panel :tab] :docs)))