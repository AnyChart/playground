(ns playground.settings-window.external-resources.subs
  (:require [re-frame.core :as rf]))

;; Resources data
(rf/reg-sub :settings.external-resources/themes
            (fn [db _] (-> db :settings :external-resources :data :themes)))

(rf/reg-sub :settings.external-resources/locales
            (fn [db _] (-> db :settings :external-resources :data :locales)))

(rf/reg-sub :settings.external-resources/maps-groups
            (fn [db _] (-> db :settings :external-resources :data :geodata :maps-groups)))

(rf/reg-sub :settings.external-resources/binaries-groups
            (fn [db _] (-> db :settings :external-resources :data :modules :binaries-groups)))

(rf/reg-sub :settings.external-resources/css
            (fn [db _] (-> db :settings :external-resources :data :css)))


;; Selects - get selected item
(rf/reg-sub :settings.external-resources/selected-resource
            (fn [db [_ type]]
              (-> db :settings :external-resources type :url)))


;; Selects buttons - is resource added (js/css)
(rf/reg-sub :settings.external-resources/added-js?
            (fn [db [_ type]]
              (let [url (-> db :settings :external-resources type :url)
                    scripts (-> db :sample :scripts)]
                (some (fn [script] (= script url)) scripts))))

(rf/reg-sub :settings.external-resources/added-css?
            (fn [db [_ type]]
              (let [url (-> db :settings :external-resources type :url)
                    styles (-> db :sample :styles)]
                (some (fn [script] (= script url)) styles))))


;; Resources selected version
(rf/reg-sub :settings.external-resources/selected-version
            (fn [db _] (-> db :settings :selected-version)))