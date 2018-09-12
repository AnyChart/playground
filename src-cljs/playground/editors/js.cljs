(ns playground.editors.js
  (:require [re-frame.core :as rf]))


(def max-window-width 650)


(defn create-editor [type value mode]
  ;(utils/log "create-editor: " type value mode)
  (let [editor-name (str (name type) "-editor")
        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value          value
                                    :lineNumbers    true
                                    :mode           {:name mode}
                                    :scrollbarStyle "overlay"}))]
    (.on cm "change" (fn [cm change]
                       (rf/dispatch [:change-code type (.getValue cm)])))
    (rf/dispatch [:change-code type (.getValue cm)])
    cm))


(defn window-height []
  (or (.-innerHeight js/window)
      (.-clientHeight (.-documentElement js/document))
      (.-clientHeight (.-body js/document))))


(defn window-width []
  (or (.-innerWidth js/window)
      (.-clientWidth (.-documentElement js/document))
      (.-clientWidth (.-body js/document))))


(defn small-window-width? []
  (< (window-width) max-window-width))


(defn big-window-width? []
  (>= (window-width) max-window-width))


(defn editors-margin-top []
  (if (< (window-width) 1060) 116 58))


(defn editors-height []
  (- (window-height)
     (editors-margin-top)                                   ; header height
     70                                                     ; footer height
     ))


(defn init []
  ;; hide or show editors copy buttons
  ;; TODO: remake it without re-frame pipeline, cause it's pollute re-frisk event history
  (js/setInterval (fn [_]
                    (let [code-editor (.getElementById js/document "code-editor")
                          style-editor (.getElementById js/document "style-editor")
                          markup-editor (.getElementById js/document "markup-editor")]
                      (when (and code-editor style-editor markup-editor)
                        (rf/dispatch [:editors/code-width-change (.-offsetWidth code-editor)])
                        (rf/dispatch [:editors/style-width-change (.-offsetWidth style-editor)])
                        (rf/dispatch [:editors/markup-width-change (.-offsetWidth markup-editor)]))))
                  50)

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