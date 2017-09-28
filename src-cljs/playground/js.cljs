(ns playground.js
  (:require [playground.utils :as utils]
            [re-frame.core :as rf]))


(defn click-handler [e]
  (when (zero? (.-length (.closest (js/$ (.-target e)) ".hide-outside")))
    ;(utils/log "Hide!")
    (rf/dispatch [:settings/hide])
    (rf/dispatch [:left-menu/close])))

(defn init []
  (.addEventListener js/document "click" click-handler))

(init)