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


(defn create-editor [editor-name text]
  (let [cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value       text
                                    :lineNumbers false
                                    :readOnly    true
                                    :mode        "text/html"
                                    }))]
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
          cm (create-editor "embed-iframe-editor" iframe-embed-text)]
      (-> db
          (assoc-in [:embed :iframe-editor] cm)
          (assoc-in [:embed :clipboard] (js/Clipboard. "#copy-embed-iframe"
                                                       (clj->js {:text (fn [] (.getValue cm))})))))))



(defn scripts [scripts]
  (clojure.string/join "\n"
                       (map (fn [script]
                              (str "<script src=\"" script "\"></script>")) scripts)))

(defn styles [db]
  (let [styles (map (fn [style]
                      (str "ac_add_style('" style "');")) (-> db :sample :styles))]
    (str "<script>
function ac_add_style(url){
\tvar el = document.createElement('link');
\tel.rel='stylesheet'; el.type='text/css'; el.media='all'; el.href=url;
\tdocument.getElementsByTagName('head')[0].appendChild(el);
}\n"
         (clojure.string/join "\n" styles) "\n
var ac_css = document.getElementById(\"ac_style_" (-> db :sample :url) "\").innerHTML;
var ac_style = document.createElement('style');
if (ac_style.styleSheet) ac_style.styleSheet.cssText = ac_css;
else ac_style.appendChild(document.createTextNode(ac_css));
document.getElementsByTagName('head')[0].appendChild(ac_style);
</script>")))

(defn embed-plain-text [db]
  (str
    "<div id=\"anychart-embed-" (-> db :sample :url) "\" class=\"anychart-embed\">\n"
    (scripts (-> db :sample :scripts)) "\n"
    "<div id=\"ac_style_" (-> db :sample :url) "\" style=\"display:none;\">\n" (-> db :sample :style) "\n</div>\n"
    (styles db) "\n"
    (-> db :sample :markup) "\n"
    "<script>\n" (-> db :sample :code) "\n</script>\n"
    "</div>"))

(rf/reg-event-db
  :embed/create-plain-html-editor
  (fn [db _]
    (let [cm (create-editor "embed-plain-html-editor" (embed-plain-text db))]
      (-> db
          (assoc-in [:embed :iframe-editor] cm)
          (assoc-in [:embed :clipboard] (js/Clipboard. "#copy-embed-plain-html"
                                                       (clj->js {:text (fn [] (.getValue cm))})))))))