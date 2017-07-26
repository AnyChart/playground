(ns playground.editors.events
  (:require [re-frame.core :as rf]
            [playground.editors.js :as editors-js]))

;;======================================================================================================================
;; Editors
;;======================================================================================================================
(rf/reg-event-db
  :create-editors
  (fn [db _]
    (let [markup-editor (editors-js/create-editor :markup (-> db :sample :markup) "text/html")
          style-editor (editors-js/create-editor :style (-> db :sample :style) "css")
          code-editor (editors-js/create-editor :code (-> db :sample :code) "javascript")]
      (-> db
          ;; editors
          (assoc-in [:editors :markup-editor] markup-editor)
          (assoc-in [:editors :style-editor] style-editor)
          (assoc-in [:editors :code-editor] markup-editor)
          ;; copy to clipboard buttons
          (assoc-in [:editors :markup-editor-clipboard]
                    (js/Clipboard. "#markup-editor-copy"
                                   (clj->js {:text (fn [] (.getValue markup-editor))})))
          (assoc-in [:editors :style-editor-clipboard]
                    (js/Clipboard. "#style-editor-copy"
                                   (clj->js {:text (fn [] (.getValue style-editor))})))
          (assoc-in [:editors :code-editor-clipboard]
                    (js/Clipboard. "#code-editor-copy"
                                   (clj->js {:text (fn [] (.getValue code-editor))})))))))

(rf/reg-event-db
  :resize-window
  (fn [db _]
    (assoc-in db [:editors :editors-height] (editors-js/editors-height))))

;;======================================================================================================================
;; Editors views
;;======================================================================================================================

(rf/reg-event-db
  :view/left
  (fn [db _]
    (assoc-in db [:editors :view] :left)))

(rf/reg-event-db
  :view/right
  (fn [db _]
    (assoc-in db [:editors :view] :right)))

(rf/reg-event-db
  :view/bottom
  (fn [db _]
    (assoc-in db [:editors :view] :bottom)))

(rf/reg-event-db
  :view/top
  (fn [db _]
    (assoc-in db [:editors :view] :top)))


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
