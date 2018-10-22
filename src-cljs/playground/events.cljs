(ns playground.events
  (:require-macros [hiccups.core :as h])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [playground.editors.js :as editors-js]
            [playground.utils.utils :as common-utils]
            [alandipert.storage-atom :refer [local-storage session-storage] :as storage-atom]
            [playground.views.iframe :as iframe-view]
            [hiccups.runtime :as hiccupsrt]
            [playground.settings-window.javascript-tab.version-detect :as version-detect]
            [playground.settings-window.javascript-tab.events :refer [detect-version-interceptor]]
            [playground.interceptors :refer [session-storage-sample-interceptor]]))


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
          ls (local-storage (atom default-prefs) :prefs)
          ss (session-storage (atom {:sample (:sample data)}) (-> data :sample :id))]
      ;; add default props
      (when (not= (merge default-prefs @ls) @ls)
        (reset! ls (merge default-prefs @ls)))
      ; clear localstorage
      ; (swap! ls assoc :hidden-tips [])
      ; (swap! ls assoc :hidden-types [])
      ; (utils/log (clj->js @ls))
      (when-not (-> data :sample :version-id)
        (.replaceState (.-history js/window) nil nil (common-utils/sample-url-with-version (:sample data))))
      (let [previous-resized-view (or (:view data) (:view @ls) :right)
            view (if (editors-js/small-window-width?)
                   :vertical
                   previous-resized-view)]
        {:db         {:editors         {:editors-height        (editors-js/editors-height)
                                        :editors-margin-top    (editors-js/editors-margin-top)
                                        :view                  view
                                        :previous-resized-view previous-resized-view
                                        :iframe-update         0

                                        :code                  {:settings-menu {:show false}
                                                                :autocomplete  true}}
                      :sample          (:sample data)
                      :saved-sample    (:sample data)
                      :templates       (:templates data)
                      :user            (:user data)
                      :datasets        (:datasets data)
                      :versions-names  (:versions-names data)

                      :settings        {:show             false
                                        :tab              :javascript
                                        :selected-version nil
                                        :general-tab      {:tags (map (fn [tag] {:name tag :selected false}) (-> data :sample :tags))}}
                      :embed           {:show    (:embed-show data)
                                        :tab     :embed
                                        :sub-tab :html
                                        :props   {:id     (common-utils/embed-name (-> data :sample))
                                                  :class  "anychart-embed"
                                                  :width  "600px"
                                                  :height "450px"}}
                      :tips            {:current []
                                        :queue   []}
                      :left-panel      {:tab       :docs
                                        :collapsed false}
                      :left-menu       {:show           false
                                        :support-expand false}
                      :view-menu       {:show false}
                      :create-menu     {:show false}
                      :download-menu   {:show false}
                      :modal           {:show false}
                      :changes-window  {:show    false
                                        :changes []
                                        :expand  false}
                      :search          {:show        false
                                        :hints       []
                                        :query-hints []}
                      :local-storage   ls
                      :session-storage ss}
         :dispatch-n [[:update-select-version]
                      [:settings.external-resources/init-version]
                      [:search-hints-request]
                      [:changes-window/check-visibility]]}))))


(rf/reg-event-fx
  :re-init
  (fn [{db :db} [_ sample]]
    {:db         (-> db
                     (assoc-in [:sample] sample)
                     (assoc-in [:changes-window :expand] false)
                     (assoc-in [:session-storage]
                               (session-storage (atom {:sample sample}) (:id sample))))
     :dispatch-n [[:sync-saved-sample]
                  [:run]
                  [:update-code (:code sample)]
                  [:update-markup (:markup sample)]
                  [:update-style (:style sample)]
                  [:changes-window/check-visibility]]}))


(rf/reg-event-db
  :change-code
  [session-storage-sample-interceptor]
  (fn [db [_ type code]]
    (assoc-in db [:sample type] code)))


(rf/reg-event-db
  :change-code-code
  [session-storage-sample-interceptor]
  (fn [db [_ code]]
    (when-let [timer (-> db :left-panel :docs :timer)]
      (js/clearTimeout timer))
    (-> db
        (assoc-in [:sample :code] code)
        (assoc-in [:left-panel :docs :timer]
                  (js/setTimeout #(rf/dispatch [:tern/udpate-anychart-defs]) 1000)))))


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
  [session-storage-sample-interceptor]
  (fn [{db :db} [_ {sample :sample :as data}]]
    (if (= :ok (:status data))
      (do
        ;; remove changes for base sample
        (storage-atom/remove-session-storage! (-> db :sample :id))
        {:db         (-> db
                         (assoc-in [:sample] sample)
                         (assoc-in [:sample :new] false)
                         (assoc-in [:sample :latest] true)
                         (assoc-in [:session-storage]
                                   (session-storage (atom {:sample sample}) (:id sample))))
         :dispatch-n [[:run]
                      [:sync-saved-sample]]
         :update-url sample})
      {:db       db
       :dispatch [:save-error "bad status"]})))


(rf/reg-event-db
  :save-error
  (fn [db [_ error]]
    (println "Save error!" error)
    (js/alert "Save error!")
    db))


;;======================================================================================================================
;; Fork sample
;;======================================================================================================================
(rf/reg-event-db
  :fork
  (fn [db _]
    (println "Fork")
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (POST "/fork"
          {:params        {:sample (common-utils/prepare-sample (:sample db))}
           :handler       #(rf/dispatch [:fork-response %1])
           :error-handler #(rf/dispatch [:fork-error %1])})
    db))


(rf/reg-event-fx
  :fork-response
  [session-storage-sample-interceptor]
  (fn [{db :db} [_ {sample :sample :as data}]]
    (storage-atom/remove-session-storage! (-> db :sample :id))
    (if (= :ok (:status data))
      (do                                                   ;;
        ;; (set! (.-location js/window) (str "/" (:hash data)))
        {:db         (-> db
                         (assoc-in [:sample] sample)
                         (assoc-in [:sample :new] false)
                         (assoc-in [:sample :latest] true)
                         (assoc-in [:session-storage]
                                   (session-storage (atom {:sample sample}) (:id sample))))
         :dispatch-n [[:run]
                      [:sync-saved-sample]]
         :update-url sample})
      {:db       db
       :dispatch [:fork-error "bad status"]})))


(rf/reg-event-db
  :fork-error
  (fn [db [_ error]]
    (println "Fork error!" error)
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
    (println "Search hints request error" error)
    db))


(rf/reg-event-db
  :update-select-version
  (fn [db _]
    (-> db (assoc-in [:settings :selected-version]
                     (or (:version (version-detect/detect-version (-> db :sample :scripts)))
                         "latest")))))


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
  (fn [sample]
    (.pushState (.-history js/window) nil nil (str "/" (:url sample)
                                                   (when (pos? (:version sample))
                                                     (str "/" (:version sample)))))))