(ns playground.sidemenu.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :left-menu/show (fn [db _] (-> db :left-menu :show)))