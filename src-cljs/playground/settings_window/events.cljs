(ns playground.settings-window.events
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [playground.settings-window.data :as external-resources]
            [playground.utils :as utils]
            ))

;;======================================================================================================================
;; Settings
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (-> db
        (assoc-in [:settings :show] true)
        ;; clear new added tips
        (assoc-in [:settings :tips] [])
        ;; set first default button value
        (assoc-in [:settings :external-resources :binary] (first external-resources/binaries))
        (assoc-in [:settings :external-resources :theme] (first external-resources/themes))
        (assoc-in [:settings :external-resources :locale] (first external-resources/locales))
        (assoc-in [:settings :external-resources :map] (first external-resources/maps)))))

(rf/reg-event-db
  :settings/hide
  (fn [db _]
    (let [local-data (-> db :local-storage deref)
          hidden-tips (:hidden-tips local-data)
          hidden-types (:hidden-types local-data)
          added-tips (reverse
                       (filter (fn [tip-url]
                                (and
                                  (every? #(not= % tip-url) hidden-tips)
                                  (every? #(not= % (:type (external-resources/get-tip tip-url (:data db)))) hidden-types)))
                              (-> db :settings :tips)))
          new-tips (take 3 (distinct (concat (map #(external-resources/get-tip % (:data db)) added-tips) (-> db :tips :current))))]
      (-> db
          (assoc-in [:settings :show] false)
          (assoc-in [:tips :current] new-tips)))))

(rf/reg-event-db
  :settings/general-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :general)))

(rf/reg-event-db
  :settings/external-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :external)))

(rf/reg-event-db
  :settings/data-sets-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :data-sets)))

(rf/reg-event-db
  :settings/change-name
  (fn [db [_ name]]
    (assoc-in db [:sample :name] name)))

(rf/reg-event-db
  :settings/change-short-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :short-description] value)))

(rf/reg-event-db
  :settings/change-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :description] value)))

;(rf/reg-event-db
;  :settings/add-script
;  (fn [db [_ value]]
;    (if (every? #(not= % value) (-> db :sample :scripts))
;      (-> db
;          (update-in [:sample :scripts] #(concat % [value]))
;          ;(update-in [:settings :tips] conj value))
;      db)))

(rf/reg-event-db
  :settings/remove-script
  (fn [db [_ value]]
    (-> db
        (update-in [:sample :scripts] (fn [scripts] (remove #(= value %) scripts)))
        (update-in [:settings :tips] (fn [tips-urls] (remove #(= value %) tips-urls))))))

(rf/reg-event-db
  :settings/remove-style
  (fn [db [_ value]]
    (update-in db [:sample :styles] (fn [styles] (remove #(= value %) styles)))))


(rf/reg-event-db
  :settings/change-tags
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :tags] (filter seq (map string/trim (string/split value #"\s"))))
        (assoc-in [:settings :tags-str] value))))


;;======================================================================================================================
;; External resources add/remove
;;======================================================================================================================
(rf/reg-event-db
  :settings.external-resources/binaries-select
  (fn [db [_ value]]
    (let [res (external-resources/get-binary-by-url value)]
      (assoc-in db [:settings :external-resources :binary] res))))

(rf/reg-event-db
  :settings.external-resources/themes-select
  (fn [db [_ value]]
    (let [res (external-resources/get-theme-by-url value)]
      (assoc-in db [:settings :external-resources :theme] res))))

(rf/reg-event-db
  :settings.external-resources/locales-select
  (fn [db [_ value]]
    (let [res (external-resources/get-locale-by-url value)]
      (assoc-in db [:settings :external-resources :locale] res))))

(rf/reg-event-db
  :settings.external-resources/maps-select
  (fn [db [_ value]]
    (let [res (external-resources/get-map-by-url value)]
      (assoc-in db [:settings :external-resources :map] res))))


(rf/reg-event-db
  :settings.external-resources/add-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] #(concat % [url]))
          (update-in [:settings :tips] conj url)))))

(rf/reg-event-db
  :settings.external-resources/remove-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] (fn [scripts] (remove #(= url %) scripts)))
          (update-in [:settings :tips] (fn [tips-urls] (remove #(= url %) tips-urls)))))))


;;======================================================================================================================
;; Data sets
;;======================================================================================================================
(rf/reg-event-db
  :settings/add-dataset
  (fn [db [_ dataset]]
    (let [code (.getValue (-> db :code-editor))]
      (if (= (.indexOf code (:url dataset)) -1)
        ;; add dataset
        (do
          (.setValue (.getDoc (:code-editor db))
                       (str "anychart.data.loadJsonFile('" (:url dataset) "', function(data) {\n"
                            "  // use data object has following format\n"
                            "  // {\n"
                            "  // name: string,\n"
                            "  // data: Array\n"
                            "  // }\n"
                            "});\n"
                            code))
          (update-in db [:settings :tips] conj (:url dataset)))
        ;; show alert
        (do
          (js/alert "Dataset has been already added.")
          db)))))