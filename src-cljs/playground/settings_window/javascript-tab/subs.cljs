(ns playground.settings-window.javascript-tab.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :settings.javascript-tab/correct-scripts
            (fn [db _]
              (-> db :settings :javascript-tab :scripts)))


(rf/reg-sub :settings.javascript-tab/correct-tab
            (fn [db _]
              (every? true? (map :correct (-> db :settings :javascript-tab :scripts)))))