(ns playground.events
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [clojure.string :as string]
            [playground.editors.js :as editors-js]))


;; -- Event Handlers -----------------------------------------------
(rf/reg-event-db
  :init
  (fn [_ [_ data]]
    {:code           ""
     :markup         ""
     :style          ""

     :editors-height (editors-js/editors-height)
     :view           :left

     :sample         (:sample data)
     :templates      (:templates data)
     :user           (:user data)
     :data-sets      (:data-sets data)

     :settings       {:show        false
                      :tab         :general
                      :scripts-str (string/join "\n" (-> data :sample :scripts))
                      :styles-str  (string/join "\n" (-> data :sample :styles))
                      :tags-str    (string/join " " (-> data :sample :tags))}
     :embed          {:show false
                      :tab  :embed}}))


(rf/reg-event-db
  :change-code
  (fn [db [_ type code]]
    (assoc-in db [:sample type] code)))


(rf/reg-event-db
  :run
  (fn [db _]
    (.submit (.getElementById js/document "run-form"))
    db))

(rf/reg-event-db
  :save
  (fn [db _]
    (utils/log "Save")
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


;; fork
(rf/reg-event-db
  :fork
  (fn [db _]
    (utils/log "Fork")
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
