(ns playground.left-panel.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  :left-panel/show-general
  (fn [db _]
    (assoc-in db [:left-panel :tab] :general)))


(rf/reg-event-db
  :left-panel/show-docs
  (fn [db _]
    (assoc-in db [:left-panel :tab] :docs)))