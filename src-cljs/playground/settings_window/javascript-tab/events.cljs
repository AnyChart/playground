(ns playground.settings-window.javascript-tab.events
  (:require [clojure.string :as string]
            [playground.utils.utils :as common-utils]
            [re-frame.core :as rf]
            [playground.settings-window.external-resources.parser :as external-resources-parser]))


;;======================================================================================================================
;; Add/remove js
;;======================================================================================================================
(def detect-version-interceptor
  (re-frame.core/->interceptor
    :id :detect-version-interceptor
    :after (fn [context]
             (let [detected-version (external-resources-parser/detect-version (-> context :effects :db :sample :scripts))]
               ;(println :detecte-version-interceptor detected-version)
               (assoc-in context [:effects :db :settings :detected-version] detected-version)))))


(rf/reg-event-db
  :settings/add-script
  [detect-version-interceptor]
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :scripts))
      (-> db
          (update-in [:sample :scripts] #(concat % [value]))
          (update-in [:tips :queue] conj value))
      db)))


(rf/reg-event-db
  :settings/remove-script
  [detect-version-interceptor]
  (fn [db [_ value]]
    (-> db
        (update-in [:sample :scripts] (fn [scripts] (remove #(= value %) scripts)))
        (update-in [:tips :queue] (fn [tips-urls] (remove #(= value %) tips-urls))))))


(rf/reg-event-db
  :settings.external-resources/add-js-by-type
  [detect-version-interceptor]
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] #(if (or (string/ends-with? url "anychart-bundle.min.js")
                                                 (string/ends-with? url "anychart-base.min.js")
                                                 (string/ends-with? url "anychart-core.min.js"))
                                           ;; add to start, otherwise to end
                                           (cons url %)
                                           (concat % [url])))
          (update-in [:tips :queue] conj url)))))


(rf/reg-event-db
  :settings.external-resources/remove-js-by-type
  [detect-version-interceptor]
  (fn [db [_ type]]
    (let [url (-> db :settings :external-resources type :url)]
      (-> db
          (update-in [:sample :scripts] (fn [scripts] (remove #(= url %) scripts)))
          (update-in [:tips :queue] (fn [tips-urls] (remove #(= url %) tips-urls)))))))


(rf/reg-event-db
  :settings/edit-script
  [detect-version-interceptor]
  (fn [db [_ val index]]
    (-> db
        (update-in [:sample :scripts] (fn [scripts]
                                        (let [scripts (vec scripts)]
                                          (assoc scripts index val)))))))


(rf/reg-event-db
  :settings/update-scripts-order
  [detect-version-interceptor]
  (fn [db [_ old-index new-index]]
    (-> db
        (update-in [:sample :scripts] #(common-utils/reorder-list % old-index new-index)))))