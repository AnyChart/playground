(ns playground.settings-window.external-resources.events
  (:require [re-frame.core :as rf]
            [ajax.core :refer [GET POST]]
            [camel-snake-kebab.core :as kebab]
            [playground.settings-window.external-resources.parser :as parser]
            [playground.tips.tips-data :as tips-data]
            [playground.settings-window.javascript-tab.version-detect :as version-detect]
            [playground.settings-window.javascript-tab.events :refer [detect-version-interceptor]]))


;;======================================================================================================================
;; Make request and parse
;;======================================================================================================================
(rf/reg-event-db
  :settings.external-resources/on-modules-json-get
  (fn [db [_ data]]
    (let [data (clojure.walk/keywordize-keys data)
          data (parser/data data (-> db :settings :selected-version))]
      (-> db
          (assoc-in [:settings :external-resources :loading] false)
          (assoc-in [:settings :external-resources :data] data)
          (assoc-in [:tips :data] (tips-data/compose-all-data
                                    (-> data :modules :binaries)
                                    (-> data :themes)
                                    (-> data :locales)
                                    (-> data :geodata :maps)
                                    (-> data :css)
                                    (-> db :datasets)))
          ;; set first default button value
          (assoc-in [:settings :external-resources :binary] (first (-> data :modules :binaries)))
          (assoc-in [:settings :external-resources :theme] (first (-> data :themes)))
          (assoc-in [:settings :external-resources :locale] (first (-> data :locales)))
          (assoc-in [:settings :external-resources :map] (first (-> data :geodata :maps)))
          (assoc-in [:settings :external-resources :css] (first (-> data :css)))))))


(rf/reg-event-db
  :settings.external-resources/on-modules-json-error
  (fn [db [_ data]]
    (js/alert "Can't load modules.json")
    (assoc-in db [:settings :external-resources :loading] false)))


(rf/reg-fx
  :settings.external-resources/parse-modules-json
  (fn [version]
    (let [url (parser/get-modules-url (parser/get-version-url version))]
      (GET url
           {:handler       #(rf/dispatch [:settings.external-resources/on-modules-json-get %1])
            :error-handler #(rf/dispatch [:settings.external-resources/on-modules-json-error %1])}))))


(rf/reg-event-fx
  :settings.external-resources/init-version
  (fn [{:keys [db]} _]
    (let [version (-> db :settings :selected-version)]
      {:db                                             (assoc-in db [:settings :external-resources :loading] true)
       :settings.external-resources/parse-modules-json version})))


(rf/reg-event-fx
  :settings.external-resources/change-version
  [detect-version-interceptor]
  (fn [{db :db} [_ version]]
    {:db       (-> db
                   (assoc-in [:settings :selected-version] version)
                   (assoc-in [:sample :scripts] (version-detect/replace-version-scripts version (-> db :sample :scripts)))
                   (assoc-in [:sample :styles] (version-detect/replace-version-scripts version (-> db :sample :styles))))
     :dispatch [:settings.external-resources/init-version]}))


;;======================================================================================================================
;; External resources add/remove
;;======================================================================================================================
(defn get-resource-by-url [url resources]
  (first (filter #(= url (:url %)) resources)))


(rf/reg-event-db
  :settings.external-resources/binaries-select
  (fn [db [_ url]]
    (let [binaries (-> db :settings :external-resources :data :modules :binaries)
          res (get-resource-by-url url binaries)]
      (assoc-in db [:settings :external-resources :binary] res))))

(rf/reg-event-db
  :settings.external-resources/themes-select
  (fn [db [_ url]]
    (let [themes (-> db :settings :external-resources :data :themes)
          res (get-resource-by-url url themes)]
      (assoc-in db [:settings :external-resources :theme] res))))

(rf/reg-event-db
  :settings.external-resources/locales-select
  (fn [db [_ url]]
    (let [locales (-> db :settings :external-resources :data :locales)
          res (get-resource-by-url url locales)]
      (assoc-in db [:settings :external-resources :locale] res))))

(rf/reg-event-db
  :settings.external-resources/maps-select
  (fn [db [_ url]]
    (let [maps (-> db :settings :external-resources :data :geodata :maps)
          res (get-resource-by-url url maps)]
      (assoc-in db [:settings :external-resources :map] res))))

(rf/reg-event-db
  :settings.external-resources/css-select
  (fn [db [_ url]]
    (let [css (-> db :settings :external-resources :data :css)
          res (get-resource-by-url url css)]
      (assoc-in db [:settings :external-resources :css] res))))