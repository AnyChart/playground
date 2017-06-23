(ns playground.embed-window.events
  (:require [re-frame.core :as rf]
            [playground.utils.utils :as common-utils]
            [playground.utils :as utils]))

(rf/reg-event-db
  :embed/show
  (fn [db _]
    (assoc-in db [:embed :show] true)))

(rf/reg-event-db
  :embed/hide
  (fn [db _]
    (assoc-in db [:embed :show] false)))

(rf/reg-event-db
  :embed/embed-tab
  (fn [db _]
    (assoc-in db [:embed :tab] :embed)))

(rf/reg-event-db
  :embed/download-tab
  (fn [db _]
    (assoc-in db [:embed :tab] :download)))


(defn create-editor [text]
  (let [editor-name "embed-iframe-editor"
        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value       text
                                    :lineNumbers false
                                    :readOnly    true
                                    :mode        "text/html"}))]
    cm))

(defn get-iframe-embed-text [sample-iframe-url]
  (str "<iframe sandbox=\"allow-scripts allow-pointer-lock allow-same-origin
                 allow-popups allow-modals allow-forms\"
        allowtransparency=\"true\" allowfullscreen=\"true\"
        src=\"" sample-iframe-url "\"
        style=\"width:100%;height:100%;border:none;\">
</iframe>"))


(rf/reg-event-db
  :embed/create-iframe-editor
  (fn [db _]
    (let [sample-iframe-url (str "http://pg.anychart.stg" (common-utils/sample-url (:sample db)) "?view=iframe")
          iframe-embed-text (get-iframe-embed-text sample-iframe-url)
          cm (create-editor iframe-embed-text)]
      (-> db
          (assoc-in [:embed :iframe-editor] cm)
          (assoc-in [:embed :clipboard] (js/Clipboard. "#copy-embed-iframe"
                                                       (clj->js {:text (fn [] (.getValue cm))})))))))