(ns playground.editors.js
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]))


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


(defn window-height []
  (or (.-innerHeight js/window)
      (.-clientHeight (.-documentElement js/document))
      (.-clientHeight (.-body js/document))))


(defn editors-height []
  (- (window-height)
     58                                                     ; header height
     70                                                     ; foother height
     ))


(defn init []
  (.addEventListener js/window "resize" (fn [_] (rf/dispatch [:resize-window])))
  ;; for closing code editor context menu
  (.addEventListener js/window "mouseup"
                     (fn [e]
                       (let [code-menu (.getElementById js/document "code-context-menu")
                             btn (.getElementById js/document "code-editor-settings-button")]
                         (when (and code-menu
                                    btn
                                    (not (.contains code-menu (.-target e)))
                                    (not (.contains btn (.-target e))))
                           (rf/dispatch [:editors.code-settings/hide]))))))

(init)