(ns playground.editors.events
  (:require [re-frame.core :as rf]
            [playground.editors.js :as editors-js]
            [playground.utils.utils :as utils]
            [ajax.core :refer [GET POST]]))


;;======================================================================================================================
;; Editors
;;======================================================================================================================
(rf/reg-event-fx
  :create-editors
  (fn [{db :db} _]
    (let [markup-editor (editors-js/create-editor :markup (-> db :sample :markup) "text/html")
          style-editor (editors-js/create-editor :style (-> db :sample :style) "css")
          code-editor (editors-js/create-editor :code (-> db :sample :code) "javascript")]
      {:db (-> db
               ;; editors
               (assoc-in [:editors :markup :editor] markup-editor)
               (assoc-in [:editors :style :editor] style-editor)
               (assoc-in [:editors :code :editor] code-editor)
               ;; copy to clipboard buttons
               (assoc-in [:editors :markup :editor-clipboard]
                         (js/Clipboard. "#markup-editor-copy"
                                        (clj->js {:text (fn [] (.getValue markup-editor))})))
               (assoc-in [:editors :style :editor-clipboard]
                         (js/Clipboard. "#style-editor-copy"
                                        (clj->js {:text (fn [] (.getValue style-editor))})))
               (assoc-in [:editors :code :editor-clipboard]
                         (js/Clipboard. "#code-editor-copy"
                                        (clj->js {:text (fn [] (.getValue code-editor))}))))})))


(rf/reg-event-db
  :resize-window
  (fn [db _]
    (let [view (-> db :editors :view)
          previous-resized-view (-> db :editors :previous-resized-view)
          [new-view new-prev-resized-view] (if (not= view :standalone)
                                             (cond
                                               (and (editors-js/small-window-width?)
                                                    (not= view :vertical)) [:vertical view]
                                               (and (editors-js/big-window-width?)
                                                    (= view :vertical)
                                                    previous-resized-view) [previous-resized-view nil]
                                               :else [view previous-resized-view])
                                             [view previous-resized-view])]
      (-> db
          (assoc-in [:editors :editors-height] (editors-js/editors-height))
          (assoc-in [:editors :editors-margin-top] (editors-js/editors-margin-top))
          (assoc-in [:editors :view] new-view)
          (assoc-in [:editors :previous-resized-view] new-prev-resized-view)))))


;;======================================================================================================================
;; Editors views
;;======================================================================================================================
;(defn push-state [url history-index]
;  (js-utils/log "push state " history-index)
;  (.replaceState (.-history js/window) history-index nil url))
;
;
;(defn different-paths [url]
;  (not= (.-pathname (.-location js/document)) url))


(defn push-state-if-need [url]
  (when (not= (.-pathname (.-location js/document)) url)
    ;(js-utils/log "Push state" url " : " (.-pathname (.-location js/document)))
    (.pushState (.-history js/window) 1 nil url)))


(defn update-view [db view]
  (push-state-if-need (utils/sample-url (:sample db)))
  (swap! (:local-storage db) assoc :view view))


(rf/reg-event-db
  :view/editor
  (fn [db _]
    ;; TODO : to effects
    (push-state-if-need (utils/sample-url (:sample db)))
    (assoc-in db [:editors :view] (-> db :local-storage deref :view))))


(rf/reg-event-db
  :view/left
  (fn [db _]
    (update-view db :right)
    (-> db
        (assoc-in [:editors :view] :left)
        (assoc-in [:editors :previous-resized-view] nil))))


(rf/reg-event-db
  :view/right
  (fn [db _]
    (update-view db :right)
    (-> db
        (assoc-in [:editors :view] :right)
        (assoc-in [:editors :previous-resized-view] nil))))


(rf/reg-event-db
  :view/bottom
  (fn [db _]
    (update-view db :bottom)
    (-> db
        (assoc-in [:editors :view] :bottom)
        (assoc-in [:editors :previous-resized-view] nil))))


(rf/reg-event-db
  :view/top
  (fn [db _]
    (update-view db :top)
    (-> db
        (assoc-in [:editors :view] :top)
        (assoc-in [:editors :previous-resized-view] nil))))


(rf/reg-event-fx
  :view/standalone
  (fn [{db :db} _]
    (let [sample-standalone-url (utils/sample-standalone-url (:sample db))]
      (push-state-if-need sample-standalone-url)
      {:db (-> db
               (assoc-in [:editors :view] :standalone)
               (assoc-in [:editors :previous-resized-view] nil))})))


(rf/reg-event-fx
  :location-change
  (fn [{db :db} [_ url id standalone?]]
    ;(js-utils/log "URL:" url " ID:" id " Standalone:" standalone?
    ;              "Current sample url:" (-> db :sample :url) " Current version:" (-> db :sample :version)
    ;              (= id (-> db :sample :version)))
    (let [new-db (if standalone?
                   (assoc-in db [:editors :view] :standalone)
                   (assoc-in db [:editors :view] (-> db :local-storage deref :view)))]
      (when-not (and (= url (-> db :sample :url))
                     (or (nil? id)
                         (= id (-> db :sample :version))))
        (POST (str "/" url (when id (str "/" id)) "/data")
              {:params        {}
               :handler       #(rf/dispatch [:data-response %1])
               :error-handler #(rf/dispatch [:data-error %1])}))
      {:db new-db})))


(rf/reg-event-fx
  :data-response
  (fn [{db :db} [_ data]]
    {:dispatch [:re-init data]}))


(rf/reg-event-db
  :data-error
  (fn [db [_ error]]
    (println "Data error!" error)
    (js/alert "Data error!")
    db))


;;======================================================================================================================
;; Code context menu
;;======================================================================================================================
(rf/reg-event-db
  :editors.code-settings/show
  (fn [db _]
    (update-in db [:editors :code-settings :show] not)))


(rf/reg-event-db
  :editors.code-settings/hide
  (fn [db _]
    ;TODO: eliminate dispatch in event handler
    (rf/dispatch [:tips/add-from-queue])
    (assoc-in db [:editors :code-settings :show] false)))


;;======================================================================================================================
;; Change editors code/style/markup
;;======================================================================================================================
(rf/reg-event-fx
  :update-code
  (fn [{db :db} [_ s]]
    {:update-code [db s]}))


(rf/reg-event-fx
  :update-markup
  (fn [{db :db} [_ s]]
    {:update-markup [db s]}))


(rf/reg-event-fx
  :update-style
  (fn [{db :db} [_ s]]
    {:update-style [db s]}))


(rf/reg-fx
  :update-code
  (fn [[db s]]
    (let [cm (-> db :editors :code :editor)]
      (when (not= (.getValue cm) s)
        (.setValue (.getDoc cm) s)))))


(rf/reg-fx
  :update-markup
  (fn [[db s]]
    (let [cm (-> db :editors :markup :editor)]
      (when (not= (.getValue cm) s)
        (.setValue (.getDoc cm) s)))))


(rf/reg-fx
  :update-style
  (fn [[db s]]
    (let [cm (-> db :editors :style :editor)]
      (when (not= (.getValue cm) s)
        (.setValue (.getDoc cm) s)))))


; ======================================================================================================================
;; Hide or show editor COPY BUTTONS
;;======================================================================================================================
(rf/reg-event-db
  :editors/code-width-change
  (fn [db [_ width]]
    (assoc-in db [:editors :code :show-copy-button] (> width 140))))


(rf/reg-event-db
  :editors/style-width-change
  (fn [db [_ width]]
    (assoc-in db [:editors :style :show-copy-button] (> width 140))))


(rf/reg-event-db
  :editors/markup-width-change
  (fn [db [_ width]]
    (assoc-in db [:editors :markup :show-copy-button] (> width 140))))