(ns playground.editors.js
  (:require [re-frame.core :as rf]))


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
  (- (window-height) 102))

(.addEventListener js/window "resize" (fn [_] (rf/dispatch [:resize-window])))


