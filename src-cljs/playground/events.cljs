(ns playground.events
  (:require-macros [hiccups.core :as h])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [clojure.string :as string]
            [playground.editors.js :as editors-js]
            [playground.data.external-resources :as external-resources]
            [playground.utils.utils :as common-utils]
            [alandipert.storage-atom :refer [local-storage]]
            [playground.views.iframe :as iframe-view]
            [hiccups.runtime :as hiccupsrt]))


(rf/reg-event-fx
  :init
  (fn [_ [_ data]]
    (let [default-prefs {:hidden-tips  []
                         :hidden-types []
                         :view         :right}
          ls (local-storage (atom default-prefs) :prefs)]
      ;; add default props
      (when (not= (merge default-prefs @ls) @ls)
        (reset! ls (merge default-prefs @ls)))
      ; clear localstorage
      ; (swap! ls assoc :hidden-tips [])
      ; (swap! ls assoc :hidden-types [])
      ; (utils/log (clj->js @ls))
      (let [view (or (:view data) (:view @ls) :right)]
        {:db {:editors       {:editors-height (editors-js/editors-height)
                              :view           view
                              :code-settings  {:show false}}

              :sample        (:sample data)
              :saved-sample  (:sample data)
              :templates     (:templates data)
              :user          (:user data)
              :datasets      (:datasets data)

              :settings      {:show               false
                              :tab                :general
                              :external-resources {:binary (first external-resources/binaries)
                                                   :theme  (first external-resources/themes)
                                                   :locale (first external-resources/locales)
                                                   :map    (first external-resources/maps)
                                                   :css    (first external-resources/css)}
                              :general-tab        {:tags (map (fn [tag] {:name tag :selected false}) (-> data :sample :tags))}}
              :embed         {:show    false
                              :tab     :embed
                              :sub-tab :html
                              :props   {:id     (common-utils/embed-name (-> data :sample))
                                        :class  "anychart-embed"
                                        :width  "600px"
                                        :height "450px"}}
              :tips          {:current []
                              :queue   []}
              :left-meu      {:show false}
              :view-menu     {:show false}
              :create-menu   {:show false}
              :local-storage ls
              :data          (external-resources/compose-all-data (:datasets data))}
         ;:dispatch-n (list (when (= view :standalone) [:run]))
         }))))


(rf/reg-event-db
  :change-code
  (fn [db [_ type code]]
    (assoc-in db [:sample type] code)))

(rf/reg-fx
  :update-iframe
  (fn [sample]
    (let [doc (.-document (.-contentWindow (.getElementById js/document "result-iframe")))
          html (str "<!DOCTYPE html>" (h/html (iframe-view/iframe sample)))]
      (.open doc)
      (.write doc html)
      (.close doc))))

(rf/reg-event-fx
  :click-run
  (fn [{db :db} _]
    (if (= :standalone (-> db :editors :view))
      {:dispatch [:view/editor]}
      {:dispatch [:run]})))

(rf/reg-event-fx
  :run
  (fn [{db :db} _]
    {:update-iframe (-> db :sample)}))

(rf/reg-event-db
  :save
  (fn [db _]
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (POST "/save"
          {:params        {:sample (common-utils/prepare-sample (:sample db))}
           :handler       #(rf/dispatch [:save-response %1])
           :error-handler #(rf/dispatch [:save-error %1])})
    db))

(rf/reg-event-fx
  :save-response
  (fn [{db :db} [_ data]]
    (if (= :ok (:status data))
      {:db         (-> db
                       (assoc-in [:sample :version-id] nil)
                       (assoc-in [:sample :new] false)
                       (assoc-in [:sample :url] (:hash data))
                       (assoc-in [:sample :version] (:version data))
                       (assoc-in [:sample :owner-id] (:owner-id data)))
       :dispatch   [:sync-saved-sample]
       :update-url data}
      {:db       db
       :dispatch [:save-error "bad status"]})))

(rf/reg-event-db
  :save-error
  (fn [db [_ error]]
    (utils/log "Save error!" error)
    (js/alert "Save error!")
    db))


(rf/reg-event-db
  :fork
  (fn [db _]
    (utils/log "Fork")
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (POST "/fork"
          {:params        {:sample (common-utils/prepare-sample (:sample db))}
           :handler       #(rf/dispatch [:fork-response %1])
           :error-handler #(rf/dispatch [:fork-error %1])})
    db))

(rf/reg-event-fx
  :fork-response
  (fn [{db :db} [_ data]]
    (if (= :ok (:status data))
      {:db         (-> db
                       (assoc-in [:sample :version-id] nil)
                       (assoc-in [:sample :new] false)
                       (assoc-in [:sample :url] (:hash data))
                       (assoc-in [:sample :version] (:version data))
                       (assoc-in [:sample :owner-id] (:owner-id data)))
       :dispatch   [:sync-saved-sample]
       :update-url data}
      {:db       db
       :dispatch [:fork-error "bad status"]})))

(rf/reg-event-db
  :fork-error
  (fn [db [_ error]]
    (utils/log "Fork error!" error)
    (js/alert "Fork error!")
    db))

(rf/reg-event-db :sync-saved-sample
                 (fn [db _]
                   (assoc db :saved-sample (:sample db))))


(rf/reg-event-db :view-menu/toggle (fn [db _] (update-in db [:view-menu :show] not)))
(rf/reg-event-db :view-menu/close (fn [db _] (assoc-in db [:view-menu :show] false)))
(rf/reg-event-db :view-menu/show (fn [db _] (assoc-in db [:view-menu :show] true)))

(rf/reg-event-db :create-menu/toggle (fn [db _] (update-in db [:create-menu :show] not)))
(rf/reg-event-db :create-menu/close (fn [db _] (assoc-in db [:create-menu :show] false)))
(rf/reg-event-db :create-menu/show (fn [db _] (assoc-in db [:create-menu :show] true)))

;;======================================================================================================================
;; Effects
;;======================================================================================================================


(rf/reg-fx
  :update-url
  (fn [data]
    (.pushState (.-history js/window) nil nil (str "/" (:hash data)
                                                   (when (pos? (:version data))
                                                     (str "/" (:version data)))))))