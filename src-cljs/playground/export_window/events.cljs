(ns playground.export-window.events
  (:require-macros [hiccups.core :as h])
  (:require [re-frame.core :as rf]
            [playground.utils.utils :as common-utils]
            [playground.utils :as utils]
            [playground.views.iframe :as iframe-view]
            [hiccups.runtime :as hiccupsrt]
            [clojure.string :as string]))

;;======================================================================================================================
;; Main
;;======================================================================================================================
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

;;======================================================================================================================
;; Sub tabs
;;======================================================================================================================
(rf/reg-event-db
  :embed/html-sub-tab
  (fn [db _] (assoc-in db [:embed :sub-tab] :html)))

(rf/reg-event-db
  :embed/iframe-sub-tab
  (fn [db _] (assoc-in db [:embed :sub-tab] :iframe)))

(rf/reg-event-db
  :embed/iframe2-sub-tab
  (fn [db _] (assoc-in db [:embed :sub-tab] :iframe2)))


(defn create-editor [editor-name text]
  (let [cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value          text
                                    :lineNumbers    false
                                    :readOnly       true
                                    :mode           "text/html"
                                    :scrollbarStyle "overlay"}))]
    cm))


;;======================================================================================================================
;; Plain HTML
;;======================================================================================================================
(defn scripts [scripts]
  (clojure.string/join "\n"
                       (map (fn [script]
                              (str "<script src=\"" script "\"></script>")) scripts)))


(defn styles [db]
  (let [styles (map (fn [style]
                      (str "ac_add_link('" style "');")) (-> db :sample :styles))]
    (str "<script>(function(){
function ac_add_to_head(el){\n\tvar head = document.getElementsByTagName('head')[0];\n\thead.insertBefore(el,head.firstChild);\n}
function ac_add_link(url){
\tvar el = document.createElement('link');
\tel.rel='stylesheet';el.type='text/css';el.media='all';el.href=url;
\tac_add_to_head(el);
}
function ac_add_style(css){
\tvar ac_style = document.createElement('style');
\tif (ac_style.styleSheet) ac_style.styleSheet.cssText = css;
\telse ac_style.appendChild(document.createTextNode(css));
\tac_add_to_head(ac_style);
}
"
         (clojure.string/join "\n" styles) "\n"
         "ac_add_style(document.getElementById(\"ac_style_" (-> db :embed :props :id) "\").innerHTML);\n"
         "ac_add_style(\"." (-> db :embed :props :class) "-" (-> db :embed :props :id) "{width:" (-> db :embed :props :width) ";height:" (-> db :embed :props :height) ";}\");"
         "\n})();</script>")))

(defn embed-plain-text [db]
  (str
    "<div id=\"anychart-embed-" (-> db :embed :props :id) "\" class=\"" (-> db :embed :props :class) " " (-> db :embed :props :class) "-" (-> db :embed :props :id) "\">\n"
    (scripts (-> db :sample :scripts)) "\n"
    "<div id=\"ac_style_" (-> db :embed :props :id) "\" style=\"display:none;\">\n" (-> db :sample :style) "\n</div>\n"
    (styles db) "\n"
    (-> db :sample :markup) "\n"
    "<script>\n" (-> db :sample :code) "\n</script>\n"
    "</div>"))


(rf/reg-event-db
  :embed/create-plain-html-editor
  (fn [db _]
    (let [cm (create-editor "embed-plain-html-editor" (embed-plain-text db))]
      (-> db
          (assoc-in [:embed :plain-html-editor] cm)
          (assoc-in [:embed :plain-html-clipboard] (js/Clipboard. "#copy-embed-plain-html"
                                                                  (clj->js {:text (fn [] (.getValue cm))})))))))


;;======================================================================================================================
;; Internal iframe
;;======================================================================================================================
(defn internal-iframe-text [db]
  (let [sample-name (-> db :embed :props :id)
        html (str "<!DOCTYPE html>" (h/html (iframe-view/iframe (:sample db))))
        html (string/replace html #"/" "\\/")
        html (string/replace html #"\"" "\\\"")
        html (string/replace html #"\\n" "\\\\n")
        html (string/replace html #"\n" "\\n")]
    (str "<iframe id=\"anychart-iframe-embed-" sample-name "\" src=\"about:blank\" frameBorder=\"0\" class=\""
         (-> db :embed :props :class) " " (-> db :embed :props :class) "-" (-> db :embed :props :id) "\"></iframe>\n"
         "<script type=\"text/javascript\">(function(){\n"
         "function ac_add_to_head(el){\n\tvar head = document.getElementsByTagName('head')[0];\n\thead.insertBefore(el,head.firstChild);\n}\n"
         "function ac_add_style(css){\n\tvar ac_style = document.createElement('style');\n\tif (ac_style.styleSheet) ac_style.styleSheet.cssText = css;\n\telse ac_style.appendChild(document.createTextNode(css));\n\tac_add_to_head(ac_style);\n}\n"
         "ac_add_style(\"." (-> db :embed :props :class) "-" (-> db :embed :props :id) "{width:" (-> db :embed :props :width) ";height:" (-> db :embed :props :height) ";}\");\n"
         "var doc = document.getElementById('anychart-iframe-embed-" sample-name "').contentWindow.document;\n"
         "doc.open();\n"
         "doc.write(\"" html "\");\n"
         "doc.close();\n})();</script>")))


(rf/reg-event-db
  :embed/create-internal-iframe-editor
  (fn [db _]
    (let [cm (create-editor "embed-internal-iframe-editor" (internal-iframe-text db))]
      (-> db
          (assoc-in [:embed :internal-iframe-editor] cm)
          (assoc-in [:embed :internal-iframe-clipboard] (js/Clipboard. "#copy-embed-internal-iframe"
                                                                       (clj->js {:text (fn [] (.getValue cm))})))))))


;;======================================================================================================================
;; Iframe
;;======================================================================================================================
(defn iframe-embed-text [db]
  (let [sample-iframe-url (str "http://pg.anychart.stg" (common-utils/canonical-url (:sample db)) "?view=iframe")]
    (str "<iframe sandbox=\"allow-scripts allow-pointer-lock allow-same-origin
                 allow-popups allow-modals allow-forms\" frameBorder=\"0\" class=\""
         (-> db :embed :props :class) " " (-> db :embed :props :class) "-" (-> db :embed :props :id) "\"
        allowtransparency=\"true\" allowfullscreen=\"true\"
        src=\"" sample-iframe-url "\">
</iframe>
<script type=\"text/javascript\">(function(){
function ac_add_to_head(el){\n\tvar head = document.getElementsByTagName('head')[0];\n\thead.insertBefore(el,head.firstChild);\n}
function ac_add_style(css){\n\tvar ac_style = document.createElement('style');\n\tif (ac_style.styleSheet) ac_style.styleSheet.cssText = css;\n\telse ac_style.appendChild(document.createTextNode(css));\n\tac_add_to_head(ac_style);\n}
ac_add_style(\"." (-> db :embed :props :class) "-" (-> db :embed :props :id) "{width:" (-> db :embed :props :width) ";height:" (-> db :embed :props :height) ";}\");
})();</script>")))


(rf/reg-event-db
  :embed/create-iframe-editor
  (fn [db _]
    (let [text (iframe-embed-text db)
          cm (create-editor "embed-iframe-editor" text)]
      (-> db
          (assoc-in [:embed :iframe-editor] cm)
          (assoc-in [:embed :iframe-clipboard] (js/Clipboard. "#copy-embed-iframe"
                                                              (clj->js {:text (fn [] (.getValue cm))})))))))

;;======================================================================================================================
;; Change embed props
;;======================================================================================================================
(rf/reg-event-db
  :embed/update-editors
  (fn [db _]
    (let [plain-html-editor (-> db :embed :plain-html-editor)
          internal-iframe-editor (-> db :embed :internal-iframe-editor)
          iframe-editor (-> db :embed :iframe-editor)]
      (.setValue (.getDoc plain-html-editor) (embed-plain-text db))
      (.setValue (.getDoc internal-iframe-editor) (internal-iframe-text db))
      (.setValue (.getDoc iframe-editor) (iframe-embed-text db))
      db)))

(rf/reg-event-db
  :embed.props/change-id
  (fn [db [_ value]]
    (rf/dispatch [:embed/update-editors])
    (assoc-in db [:embed :props :id] value)))

(rf/reg-event-db
  :embed.props/change-class
  (fn [db [_ value]]
    (rf/dispatch [:embed/update-editors])
    (assoc-in db [:embed :props :class] value)))

(rf/reg-event-db
  :embed.props/change-width
  (fn [db [_ value]]
    (rf/dispatch [:embed/update-editors])
    (assoc-in db [:embed :props :width] value)))

(rf/reg-event-db
  :embed.props/change-height
  (fn [db [_ value]]
    (rf/dispatch [:embed/update-editors])
    (assoc-in db [:embed :props :height] value)))