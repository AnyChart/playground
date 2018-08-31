(ns playground.settings-window.datasets-tab.events
  (:require [re-frame.core :as rf]))


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
    (let [code (.getValue (-> db :editors :code :editor))]
      (if-not (dataset-added? dataset code)
        ;; add dataset
        (do
          ;; TODO: make event handler clean
          (.setValue (.getDoc (-> db :editors :code :editor))
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