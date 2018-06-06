(ns playground.events
  (:require-macros [hiccups.core :as h])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as js-utils]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [playground.editors.js :as editors-js]
            [playground.utils.utils :as common-utils]
            [alandipert.storage-atom :refer [local-storage]]
            [playground.views.iframe :as iframe-view]
            [hiccups.runtime :as hiccupsrt]
            [playground.utils.utils :as utils]
            [playground.settings-window.javascript-tab.version-detect :as version-detect]
            [playground.settings-window.javascript-tab.events :refer [detect-version-interceptor]]))


;;======================================================================================================================
;; Init
;;======================================================================================================================
(rf/reg-event-fx
  :init
  [detect-version-interceptor]
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
      (when-not (-> data :sample :version-id)
        (.replaceState (.-history js/window) nil nil (utils/sample-url-with-version (:sample data))))
      (let [view (or (:view data) (:view @ls) :right)]
        {:db         {:editors        {:editors-height (editors-js/editors-height)
                                       :view           view
                                       :code-settings  {:show false}
                                       :iframe-update  0}

                      :sample         (:sample data)
                      :saved-sample   (:sample data)
                      :templates      (:templates data)
                      :user           (:user data)
                      :datasets       (:datasets data)
                      :versions-names (:versions-names data)

                      :settings       {:show             false
                                       :tab              :javascript
                                       :selected-version (or (:version (version-detect/detect-version (:scripts (:sample data))))
                                                             "latest")
                                       :general-tab      {:tags (map (fn [tag] {:name tag :selected false}) (-> data :sample :tags))}}
                      :embed          {:show    (:embed-show data)
                                       :tab     :embed
                                       :sub-tab :html
                                       :props   {:id     (common-utils/embed-name (-> data :sample))
                                                 :class  "anychart-embed"
                                                 :width  "600px"
                                                 :height "450px"}}
                      :tips           {:current []
                                       :queue   []}
                      :left-menu      {:show false}
                      :view-menu      {:show false}
                      :create-menu    {:show false}
                      :download-menu  {:show false}
                      :modal          {:show false}
                      :search         {:show        false
                                       :hints       []
                                       :query-hints []}
                      :local-storage  ls}
         :dispatch-n [[:settings.external-resources/init-version]
                      [:search-hints-request]]}))))


(rf/reg-event-fx
  :re-init
  (fn [{db :db} [_ sample]]
    {:db         (-> db
                     (assoc-in [:sample] sample))
     :dispatch-n [[:sync-saved-sample]
                  [:run]
                  [:update-code (:code sample)]
                  [:update-markup (:markup sample)]
                  [:update-style (:style sample)]]}))


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
  :on-update-iframe
  (fn [{db :db} _]
    {:update-iframe (-> db :sample)}))


(rf/reg-event-db
  :run
  (fn [db _]
    (update-in db [:editors :iframe-update] inc)))


(rf/reg-event-fx
  :click-run
  (fn [{db :db} _]
    (if (= :standalone (-> db :editors :view))
      {:dispatch [:view/editor]}
      {:dispatch [:run]})))


;;======================================================================================================================
;; Save sample
;;======================================================================================================================
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
       :dispatch-n [[:run]
                    [:sync-saved-sample]]
       :update-url data}
      {:db       db
       :dispatch [:save-error "bad status"]})))


(rf/reg-event-db
  :save-error
  (fn [db [_ error]]
    (js-utils/log "Save error!" error)
    (js/alert "Save error!")
    db))


;;======================================================================================================================
;; Fork sample
;;======================================================================================================================
(rf/reg-event-db
  :fork
  (fn [db _]
    (js-utils/log "Fork")
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
       :dispatch-n [[:run]
                    [:sync-saved-sample]]
       :update-url data}
      {:db       db
       :dispatch [:fork-error "bad status"]})))


(rf/reg-event-db
  :fork-error
  (fn [db [_ error]]
    (js-utils/log "Fork error!" error)
    (js/alert "Fork error!")
    db))


;;======================================================================================================================
;; Search hints request
;;======================================================================================================================
(rf/reg-event-db
  :search-hints-request
  (fn [db _]
    (GET "/search-hints"
         {:handler       #(rf/dispatch [:search-hints-request-response %1])
          :error-handler #(rf/dispatch [:search-hints-request-error %1])})
    db))


(rf/reg-event-db
  :search-hints-request-response
  (fn [db [_ data]]
    (assoc-in db [:search :hints] (sort (map :name data)))))


(rf/reg-event-db
  :search-hints-request-error
  (fn [db [_ error]]
    (js-utils/log "Search hints request error" error)
    db))


;;======================================================================================================================
;; Misc
;;======================================================================================================================
(rf/reg-event-db :sync-saved-sample
                 (fn [db _]
                   (assoc db :saved-sample (:sample db))))


(rf/reg-event-db :view-menu/toggle (fn [db _] (update-in db [:view-menu :show] not)))
(rf/reg-event-db :view-menu/close (fn [db _] (assoc-in db [:view-menu :show] false)))
(rf/reg-event-db :view-menu/show (fn [db _] (assoc-in db [:view-menu :show] true)))


(rf/reg-event-db :create-menu/toggle (fn [db _] (update-in db [:create-menu :show] not)))
(rf/reg-event-db :create-menu/close (fn [db _] (assoc-in db [:create-menu :show] false)))
(rf/reg-event-db :create-menu/show (fn [db _] (assoc-in db [:create-menu :show] true)))


(rf/reg-event-db :download-menu/show (fn [db _] (assoc-in db [:download-menu :show] true)))
(rf/reg-event-db :download-menu/close (fn [db _] (assoc-in db [:download-menu :show] false)))


;;======================================================================================================================
;; Effects
;;======================================================================================================================
(rf/reg-fx
  :update-url
  (fn [data]
    (.pushState (.-history js/window) nil nil (str "/" (:hash data)
                                                   (when (pos? (:version data))
                                                     (str "/" (:version data)))))))