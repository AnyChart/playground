(ns playground.editors.events
  (:require [re-frame.core :as rf]
            [playground.editors.js :as editors-js]
            [playground.utils.utils :as utils]))

;;======================================================================================================================
;; Editors
;;======================================================================================================================
(rf/reg-event-fx
  :create-editors
  (fn [{db :db} _]
    (let [markup-editor (editors-js/create-editor :markup (-> db :sample :markup) "text/html")
          style-editor (editors-js/create-editor :style (-> db :sample :style) "css")
          code-editor (editors-js/create-editor :code (-> db :sample :code) "javascript")]
      {:db       (-> db
                     ;; editors
                     (assoc-in [:editors :markup-editor] markup-editor)
                     (assoc-in [:editors :style-editor] style-editor)
                     (assoc-in [:editors :code-editor] code-editor)
                     ;; copy to clipboard buttons
                     (assoc-in [:editors :markup-editor-clipboard]
                               (js/Clipboard. "#markup-editor-copy"
                                              (clj->js {:text (fn [] (.getValue markup-editor))})))
                     (assoc-in [:editors :style-editor-clipboard]
                               (js/Clipboard. "#style-editor-copy"
                                              (clj->js {:text (fn [] (.getValue style-editor))})))
                     (assoc-in [:editors :code-editor-clipboard]
                               (js/Clipboard. "#code-editor-copy"
                                              (clj->js {:text (fn [] (.getValue code-editor))}))))
       :dispatch [:run]})))

(rf/reg-event-db
  :resize-window
  (fn [db _]
    (assoc-in db [:editors :editors-height] (editors-js/editors-height))))

;;======================================================================================================================
;; Editors views
;;======================================================================================================================
(defn update-view [db view]
  (.pushState (.-history js/window) nil nil (utils/sample-url (:sample db)))
  (swap! (:local-storage db) assoc :view view))

(rf/reg-event-db
  :view/editor
  (fn [db _]
    ;; TODO : to effects
    (.pushState (.-history js/window) nil nil (utils/sample-url (:sample db)))
    (assoc-in db [:editors :view] (-> db :local-storage deref :view))))

(rf/reg-event-db
  :view/left
  (fn [db _]
    (update-view db :left)
    (assoc-in db [:editors :view] :left)))

(rf/reg-event-db
  :view/right
  (fn [db _]
    (update-view db :right)
    (assoc-in db [:editors :view] :right)))

(rf/reg-event-db
  :view/bottom
  (fn [db _]
    (update-view db :bottom)
    (assoc-in db [:editors :view] :bottom)))

(rf/reg-event-db
  :view/top
  (fn [db _]
    (update-view db :top)
    (assoc-in db [:editors :view] :top)))

(rf/reg-event-fx
  :view/standalone
  (fn [{db :db} _]
    (let [sample-standalone-url (utils/sample-standalone-url (:sample db))]
      (.pushState (.-history js/window) nil nil sample-standalone-url)
      {:db (-> db
               (assoc-in [:editors :view] :standalone))
       ;:dispatch [:run]
       })))

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
