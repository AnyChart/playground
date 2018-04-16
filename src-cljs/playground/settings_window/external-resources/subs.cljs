(ns playground.settings-window.external-resources.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub :settings.external-resources/themes
            (fn [db _] (-> db :settings :external-resources :data :themes)))

(rf/reg-sub :settings.external-resources/locales
            (fn [db _] (-> db :settings :external-resources :data :locales)))

(rf/reg-sub :settings.external-resources/maps-groups
            (fn [db _] (-> db :settings :external-resources :data :geodata :maps-groups)))

;(rf/reg-sub :settings.external-resources/modules
;            (fn [db _] (-> db :settings :external-resources :data :modules)))

(rf/reg-sub :settings.external-resources/binaries-groups
            (fn [db _] (-> db :settings :external-resources :data :modules :binaries-groups)))

(rf/reg-sub :settings.external-resources/css
            (fn [db _] (-> db :settings :external-resources :data :css)))


(rf/reg-sub :settings.external-resources/selected-version
            (fn [db _] (-> db :settings :selected-version)))

