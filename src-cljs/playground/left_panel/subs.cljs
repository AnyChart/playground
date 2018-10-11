(ns playground.left-panel.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :left-panel/general-tab?
            (fn [db _]
              (= (-> db :left-panel :tab) :general)))


(rf/reg-sub :left-panel/docs-tab?
            (fn [db _]
              (= (-> db :left-panel :tab) :docs)))