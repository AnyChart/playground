(ns playground.settings-window.events
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [playground.data.external-resources :as external-resources]
            [playground.data.tags :as tags-data]
            [playground.utils :as utils]
            [playground.utils.utils :as common-utils]))

;;======================================================================================================================
;; Settings
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (-> db
        (assoc-in [:settings :show] true)
        ;; set first default button value
        (assoc-in [:settings :external-resources :binary] (first external-resources/binaries))
        (assoc-in [:settings :external-resources :theme] (first external-resources/themes))
        (assoc-in [:settings :external-resources :locale] (first external-resources/locales))
        (assoc-in [:settings :external-resources :map] (first external-resources/maps)))))

(rf/reg-event-db
  :settings/hide
  (fn [db _]
    ;TODO: eliminate dispatch in event handler
    (rf/dispatch [:tips/add-from-queue])
    (-> db
        (assoc-in [:settings :show] false))))

(rf/reg-event-db
  :settings/general-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :general)))

(rf/reg-event-db
  :settings/javascript-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :javascript)))

(rf/reg-event-db
  :settings/css-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :css)))

(rf/reg-event-db
  :settings/datasets-tab
  (fn [db _]
    (assoc-in db [:settings :tab] :datasets)))

(rf/reg-event-db
  :settings/change-name
  (fn [db [_ name]]
    (assoc-in db [:sample :name] name)))

(rf/reg-event-db
  :settings/change-short-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :short-description] (common-utils/strip-tags value))))

(rf/reg-event-db
  :settings/change-desc
  (fn [db [_ value]]
    (assoc-in db [:sample :description] (common-utils/strip-tags value))))

(rf/reg-event-db
  :settings/add-script
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :scripts))
      (-> db
          (update-in [:sample :scripts] #(concat % [value]))
          (update-in [:tips :queue] conj value))
      db)))

(rf/reg-event-db
  :settings/remove-script
  (fn [db [_ value]]
    (-> db
        (update-in [:sample :scripts] (fn [scripts] (remove #(= value %) scripts)))
        (update-in [:tips :queue] (fn [tips-urls] (remove #(= value %) tips-urls))))))

(rf/reg-event-db
  :settings/add-style
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :styles))
      (-> db
          (update-in [:sample :styles] #(concat % [value]))
          (update-in [:tips :queue] conj value))
      db)))

(rf/reg-event-db
  :settings/remove-style
  (fn [db [_ value]]
    (update-in db [:sample :styles] (fn [styles] (remove #(= value %) styles)))))


(rf/reg-event-db
  :settings/refresh-tags
  (fn [db _]
    (let [tags-by-code (tags-data/get-tags-by-code (-> db :sample :code))
          deleted-tags (-> db :sample :deleted-tags)
          new-tags (distinct (concat (-> db :sample :tags)
                                     (vec (clojure.set/difference
                                            (set tags-by-code)
                                            (set deleted-tags)))))
          ;; to set :settings :general-tab :tags
          settings-tags (-> db :settings :general-tab :tags)
          new-settings-tags (map (fn [tag-name]
                                   {:name     tag-name
                                    :selected (boolean (:selected (first (filter #(= tag-name (:name %)) settings-tags))))})
                                 new-tags)]
      (-> db
          (assoc-in [:sample :tags] new-tags)
          (assoc-in [:settings :general-tab :tags] new-settings-tags)))))


;;======================================================================================================================
;; General tabs: tags
;;======================================================================================================================
(rf/reg-event-db
  :settings/select-tag
  (fn [db [_ tag-name]]
    (-> db
        (update-in [:settings :general-tab :tags]
                   (fn [tags]
                     (map (fn [tag]
                            (if (= (:name tag) tag-name)
                              (update tag :selected not)
                              tag)) tags))))))

(rf/reg-event-fx
  :settings/tags-backspace
  (fn [{db :db} _]
    (let [last-tag (-> db :settings :general-tab :tags last)]
      (if-not (:selected last-tag)
        {:dispatch [:settings/select-tag (:name last-tag)]}
        {:dispatch [:settings/remove-tag (:name last-tag)]}))))

(rf/reg-event-db
  :settings/remove-tag
  (fn [db [_ value]]
    (-> db
        (update-in [:sample :tags] #(remove (partial = value) %))
        (update-in [:settings :general-tab :tags] #(remove (fn [tag] (= value (:name tag))) %))
        ;; add to deleted tags
        (update-in [:sample :deleted-tags] (fn [del-tags]
                                             (if (tags-data/anychart-tag? value)
                                               (distinct (conj del-tags value))
                                               del-tags))))))

(rf/reg-event-db
  :settings/add-tag
  (fn [db [_ value]]
    (if (every? (partial not= value) (-> db :sample :tags))
      (-> db
          (update-in [:sample :tags] concat [value])
          (update-in [:settings :general-tab :tags] concat [{:name value :selected false}])
          (update-in [:sample :deleted-tags] (fn [del-tags]
                                               (remove (partial = value) del-tags))))
      db)))

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
  :settings.external-resources/css-select
  (fn [db [_ value]]
    (let [res (external-resources/get-css-by-url value)]
      (assoc-in db [:settings :external-resources :css] res))))

;;======================================================================================================================
;; Add/remove js
;;======================================================================================================================
(rf/reg-event-db
  :settings.external-resources/add-js-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] #(concat % [url]))
          (update-in [:tips :queue] conj url)))))

(rf/reg-event-db
  :settings.external-resources/remove-js-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] (fn [scripts] (remove #(= url %) scripts)))
          (update-in [:tips :queue] (fn [tips-urls] (remove #(= url %) tips-urls)))))))


;;======================================================================================================================
;; Add/remove css
;;======================================================================================================================
(rf/reg-event-db
  :settings.external-resources/add-css-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :styles] #(concat % [url]))
          (update-in [:tips :queue] conj url)))))

(rf/reg-event-db
  :settings.external-resources/remove-css-by-type
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :styles] (fn [scripts] (remove #(= url %) scripts)))
          (update-in [:tips :queue] (fn [tips-urls] (remove #(= url %) tips-urls)))))))


;;======================================================================================================================
;; Data sets
;;======================================================================================================================
(defn dataset-added? [dataset code]
  (> (.indexOf code (:url dataset)) -1))

(rf/reg-event-db
  :settings/update-datasets
  (fn [db _]
    (let [code (-> db :sample :code)
          datasets (-> db :datasets)
          updated-datasets (map (fn [dataset]
                                  (assoc dataset :added (dataset-added? dataset code)))
                                datasets)]
      (assoc db :datasets updated-datasets))))


(rf/reg-event-fx
  :settings/add-dataset
  (fn [{:keys [db]} [_ dataset]]
    (let [code (.getValue (-> db :editors :code-editor))]
      (if-not (dataset-added? dataset code)
        ;; add dataset
        (do
          ;; TODO: make event handler clean
          (.setValue (.getDoc (-> db :editors :code-editor))
                     (str "anychart.data.loadJsonFile('" (:url dataset) "', function(data) {\n"
                          "  // use data object has following format\n"
                          "  // {\n"
                          "  // name: string,\n"
                          "  // data: Array\n"
                          "  // }\n"
                          "});\n"
                          code))
          {:db         (update-in db [:tips :queue] conj (:url dataset))
           :dispatch-n (list [:settings/update-datasets])})
        ;; show alert
        (do
          ;; TODO: eliminate side-effect
          (js/alert "Dataset has been already added.")
          {:db db})))))