(ns playground.events
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [clojure.string :as string]
            [playground.editors.js :as editors-js]
            [playground.settings-window.data :as external-resources]
            [playground.utils.utils :as common-utils]
            [playground.settings-window.data :as data]
            [alandipert.storage-atom :refer [local-storage]]))


(rf/reg-event-db
  :init
  (fn [_ [_ data]]
    (let [default-prefs {:hidden-tips  []
                         :hidden-types []
                         :view         :left}
          ls (local-storage (atom default-prefs) :prefs)]
      ;; add default props
      (when (not= (merge default-prefs @ls) @ls)
        (reset! ls (merge default-prefs @ls)))
      ; clear localstorage
      ; (swap! ls assoc :hidden-tips [])
      ; (swap! ls assoc :hidden-types [])
      ; (utils/log (clj->js @ls))
      {:editors       {:editors-height (editors-js/editors-height)
                       :view           (or (:view data) (:view @ls) :left)
                       :code-settings  {:show false}}

       :sample        (:sample data)
       :templates     (:templates data)
       :user          (:user data)
       :datasets      (:datasets data)

       :settings      {:show               false
                       :tab                :general
                       :tags-str           (string/join " " (-> data :sample :tags))
                       :external-resources {:binary (first external-resources/binaries)
                                            :theme  (first external-resources/themes)
                                            :locale (first external-resources/locales)
                                            :map    (first external-resources/maps)}}
       :embed         {:show    false
                       :tab     :embed
                       :sub-tab :html
                       :props   {:id     (common-utils/embed-name (-> data :sample))
                                 :class  "anychart-embed"
                                 :width  "600px"
                                 :height "450px"}}
       :tips          {:current []
                       :queue   []}
       :local-storage ls
       :data          (data/compose-all-data (:datasets data))})))


(rf/reg-event-db
  :change-code
  (fn [db [_ type code]]
    (assoc-in db [:sample type] code)))

(rf/reg-event-db
  :run
  (fn [db _]
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (.submit (.getElementById js/document "run-form"))
    db))

(rf/reg-event-db
  :save
  (fn [db _]
    (utils/log "Save")
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (POST "/save"
          {:params        {:sample (:sample db)}
           :handler       #(rf/dispatch [:save-response %1])
           :error-handler #(rf/dispatch [:save-error %1])})
    db))

(rf/reg-event-db
  :save-response
  (fn [db [_ data]]
    (utils/log "Save ok!" data)
    (if (= :ok (:status data))
      (do
        ;(accountant/navigate! (str "/" (:hash data)
        ;                             (when (pos? (:version data))
        ;                               (str "/" (:version data)))))
        (.pushState (.-history js/window) nil nil (str "/" (:hash data)
                                                       (when (pos? (:version data))
                                                         (str "/" (:version data)))))
        (-> db
            (assoc-in [:sample :version-id] nil)
            (assoc-in [:sample :new] false)
            (assoc-in [:sample :url] (:hash data))
            (assoc-in [:sample :version] (:version data))
            (assoc-in [:sample :owner-id] (:owner-id data))))
      (do
        (js/alert "Sample saving error")
        db))))

(rf/reg-event-db
  :save-error
  (fn [db [_ error]]
    (utils/log "Save erro!" error)
    db))


(rf/reg-event-db
  :fork
  (fn [db _]
    (utils/log "Fork")
    (when (= :standalone (-> db :editors :view))
      (rf/dispatch [:view/editor]))
    (POST "/fork"
          {:params        {:sample (:sample db)}
           :handler       #(rf/dispatch [:fork-response %1])
           :error-handler #(rf/dispatch [:fork-error %1])})
    db))

(rf/reg-event-db
  :fork-response
  (fn [db [_ data]]
    (utils/log "Fork ok!" data)
    (if (= :ok (:status data))
      (do
        ;(accountant/navigate! (str "/" (:hash data)
        ;                             (when (pos? (:version data))
        ;                               (str "/" (:version data)))))
        (.pushState (.-history js/window) nil nil (str "/" (:hash data)
                                                       (when (pos? (:version data))
                                                         (str "/" (:version data)))))
        (-> db
            (assoc-in [:sample :version-id] nil)
            (assoc-in [:sample :new] false)
            (assoc-in [:sample :url] (:hash data))
            (assoc-in [:sample :version] (:version data))
            (assoc-in [:sample :owner-id] (:owner-id data))))
      (do
        (js/alert "Sample fork error")
        db))))

(rf/reg-event-db
  :fork-error
  (fn [db [_ error]]
    (utils/log "Fork error!" error)
    db))
