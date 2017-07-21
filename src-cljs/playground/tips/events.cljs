(ns playground.tips.events
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]))

(rf/reg-event-db
  :tips.tip/close
  (fn [db [_ link]]
    (update-in db [:tips :current]
               #(remove (fn [tip] (= (:link tip) link)) %))))