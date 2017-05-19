(ns playground.events
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]
            [ajax.core :refer [GET POST]]
    ;[accountant.core :as accountant]
            [clojure.string :as string]))

(defn window-height []
  (or (.-innerHeight js/window)
      (.-clientHeight (.-documentElement js/document))
      (.-clientHeight (.-body js/document))))

(defn editors-height []
  (- (window-height) 102))

(.addEventListener js/window "resize" (fn [_] (rf/dispatch [:resize-window])))

(defn create-editor [type value mode]
  ;(utils/log "create-editor: " type value mode)
  (let [editor-name (str (name type) "-editor")
        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value       value
                                    :lineNumbers true
                                    :mode        {:name mode}}))]
    (.on cm "change" (fn [cm change]
                       (rf/dispatch [:change-code type (.getValue cm)])))
    (rf/dispatch [:change-code type (.getValue cm)])
    cm))


;; -- Event Handlers -----------------------------------------------
(rf/reg-event-db
  :pre-init
  (fn [_ [_ data]]
    {:code           ""
     :markup         ""
     :style          ""

     :editors-height (editors-height)
     :view           :left

     :sample         (:sample data)
     :templates      (:templates data)
     :user           (:user data)
     :data-sets      (:data-sets data)

     :settings       {:show        false
                      :tab         :general
                      :scripts-str (string/join "\n" (-> data :sample :scripts))
                      :styles-str  (string/join "\n" (-> data :sample :styles))
                      :tags-str    (string/join " " (-> data :sample :tags))}}))

(rf/reg-event-db
  :create-editors
  (fn [db _]
    (assoc db
      :markup-editor (create-editor :markup (-> db :sample :markup) "text/html")
      :style-editor (create-editor :style (-> db :sample :style) "css")
      :code-editor (create-editor :code (-> db :sample :code) "javascript"))))

(rf/reg-event-db
  :resize-window
  (fn [db _]
    (assoc db :editors-height (editors-height))))

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

;;======================================================================================================================
;; Settings
;;======================================================================================================================
(rf/reg-event-db
  :settings/show
  (fn [db _]
    (assoc-in db [:settings :show] true)))

(rf/reg-event-db
  :settings/hide
  (fn [db _]
    (assoc-in db [:settings :show] false)))

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

(rf/reg-event-db
  :settings/add-script
  (fn [db [_ value]]
    (if (every? #(not= % value) (-> db :sample :scripts))
      (-> db
          (update-in [:sample :scripts] #(concat % [value]))
          (update-in [:settings :scripts-str] str (str "\n" value)))
      db)))

(rf/reg-event-db
  :settings/change-scripts
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :scripts] (filter seq (map string/trim (string/split-lines value))))
        (assoc-in [:settings :scripts-str] value))))

(rf/reg-event-db
  :settings/change-styles
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :styles] (filter seq (map string/trim (string/split-lines value))))
        (assoc-in [:settings :styles-str] value))))

(rf/reg-event-db
  :settings/change-tags
  (fn [db [_ value]]
    (-> db
        (assoc-in [:sample :tags] (filter seq (map string/trim (string/split value #"\s"))))
        (assoc-in [:settings :tags-str] value))))

;;======================================================================================================================
;; Settings
;;======================================================================================================================
(rf/reg-event-db
  :view/left
  (fn [db _]
    (assoc-in db [:view] :left)))

(rf/reg-event-db
  :view/right
  (fn [db _]
    (assoc-in db [:view] :right)))

(rf/reg-event-db
  :view/bottom
  (fn [db _]
    (assoc-in db [:view] :bottom)))

(rf/reg-event-db
  :view/top
  (fn [db _]
    (assoc-in db [:view] :top)))