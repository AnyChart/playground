(ns playground.changes-window.events
  (:require [re-frame.core :as rf]
            [clojure.data :as data]
            [playground.settings-window.javascript-tab.events :refer [detect-version-interceptor]]))


(rf/reg-event-db
  :changes-window/expand
  (fn [db _]
    (-> db
        (assoc-in [:changes-window :expand] true))))


(def keyword-names {:description       "Description"
                    :create-date       "Creation Date"

                    :tags              "Tags"
                    ;:repo-id 2
                    ;:owner-fullname "AnyChart Team"
                    :markup-type       "Markup Type"
                    :code-type         "Code Type"
                    :short-description "Short Description"
                    ;:repo-title "Docs samples"
                    :name              "Name"

                    :scripts           "Scripts"
                    ;:version-id 350
                    ;:likes 0
                    ;:repo-name "docs"
                    :style             "Style"
                    :markup            "Markup"
                    ;:full-url "/docs/8.3.0/samples/quick_start_pie"
                    ;:latest true
                    ;:preview true
                    ;:deleted-tags [0 items]
                    ;:id 233419
                    ;:version-name"8.3.0"
                    ;:url "samples/quick_start_pie"
                    :code              "Code"
                    :styles            "Styles"
                    ;:owner-id 5
                    ;:version 0
                    :style-type        "Style type"
                    ;:views263
                    })


(defn get-diffs [old-sample new-sample]
  (let [diffs (data/diff old-sample new-sample)
        ks (keys (first diffs))
        diffs-names (map #(get keyword-names %) ks)]
    diffs-names))


(rf/reg-event-db
  :changes-window/check-visibility
  [detect-version-interceptor]
  (fn [db _]
    (let [session-storage-sample (-> db :session-storage deref :sample)
          session-storage-sample (select-keys session-storage-sample (keys keyword-names))

          sample (-> db :sample)
          sample (select-keys sample (keys keyword-names))

          show-changes-window (and (some? session-storage-sample)
                                   (not= sample session-storage-sample))
          diffs (get-diffs session-storage-sample sample)]
      (-> db
          (assoc-in [:changes-window :show] show-changes-window)
          (assoc-in [:changes-window :changes] diffs)))))


(rf/reg-event-fx
  :changes-window/apply-changes
  [detect-version-interceptor]
  (fn [{db :db} _]
    (let [session-storage-sample (-> db :session-storage deref :sample)]
      {:db         (-> db
                       (assoc-in [:sample] session-storage-sample)
                       (assoc-in [:changes-window :show] false))
       :dispatch-n [[:update-code (:code session-storage-sample)]
                    [:update-markup (:markup session-storage-sample)]
                    [:update-style (:style session-storage-sample)]]})))


(rf/reg-event-db
  :changes-window/discard-changes
  [detect-version-interceptor]
  (fn [db _]
    (reset! (-> db :session-storage) {:sample (-> db :sample)})
    (-> db
        (assoc-in [:changes-window :show] false))))
