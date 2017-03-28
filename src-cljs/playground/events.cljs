(ns playground.events
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]))

(defn window-height []
  (or (.-innerHeight js/window)
      (.-clientHeight (.-documentElement js/document))
      (.-clientHeight (.-body js/document))))

(.addEventListener js/window "resize" (fn [_] (rf/dispatch [:resize-window])))

(defn create-editor [type value mode]
  (let [editor-name (str (name type) "-editor")
        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value       value
                                    :lineNumbers true
                                    :mode        {:name mode}}))]
    (.on cm "change" (fn [cm change]
                       (rf/dispatch [:change-code type (.getValue cm)])))
    (rf/dispatch [:change-code type (.getValue cm)])
    cm))


;; -- Event Handlers -----------------------------------------------
(rf/reg-event-db
  :pre-init
  (fn [_ [_ data]]
    {:code           ""
     :markup         ""
     :style          ""
     :editors-height (window-height)
     :sample         data}))

(rf/reg-event-db
  :init
  (fn [db [_ data]]
    (assoc db
      :markup-editor (create-editor :markup (:markup data) "text/html")
      :style-editor (create-editor :style (:style data) "css")
      :code-editor (create-editor :code (:code data) "javascript"))))

(rf/reg-event-db
  :resize-window
  (fn [db _]
    (assoc db :editors-height (window-height))))

(rf/reg-event-db
  :change-code
  (fn [db [_ type code]]
    ;(utils/log "Change code event: " type code)
    (assoc db type code)))

(rf/reg-event-db
  :run
  (fn [db _]
    ;(utils/log "Run")
    (.submit (.getElementById js/document "run-form"))
    db))