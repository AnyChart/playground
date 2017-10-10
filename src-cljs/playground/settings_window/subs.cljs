(ns playground.settings-window.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :settings/show (fn [db _] (-> db :settings :show)))

(rf/reg-sub :settings/general-tab? (fn [db _] (= :general (-> db :settings :tab))))
(rf/reg-sub :settings/javascript-tab? (fn [db _] (= :javascript (-> db :settings :tab))))
(rf/reg-sub :settings/css-tab? (fn [db _] (= :css (-> db :settings :tab))))
(rf/reg-sub :settings/datasets-tab? (fn [db _] (= :datasets (-> db :settings :tab))))

(rf/reg-sub :settings.external-resources/added?
            (fn [db [_ type]]
              (let [url (-> db :settings :external-resources type :url)
                    scripts (-> db :sample :scripts)]
                (some (fn [script] (= script url)) scripts))))