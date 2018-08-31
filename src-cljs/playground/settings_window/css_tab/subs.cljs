(ns playground.settings-window.css-tab.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :settings.css-tab/correct-styles
            (fn [db _]
              (-> db :settings :css-tab :styles)))


(rf/reg-sub :settings.css-tab/correct-tab
            (fn [db _]
              (every? nil? (map :warning (-> db :settings :css-tab :styles)))))