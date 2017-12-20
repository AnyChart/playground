(ns playground.js
  (:require [playground.utils :as utils]
            [re-frame.core :as rf]
            [goog.events :as events]
            [secretary.core :as secretary :refer-macros [defroute]])
  (:import [goog History]
           [goog.history EventType]))


(defn click-handler [e]
  (when (zero? (.-length (.closest (js/$ (.-target e)) ".hide-outside")))
    ;(utils/log "Hide!")
    (rf/dispatch [:settings/hide])
    (rf/dispatch [:embed/hide])
    (rf/dispatch [:left-menu/close])
    (rf/dispatch [:view-menu/close])
    (rf/dispatch [:create-menu/close])))

(defn init []
  (.addEventListener js/document "click" click-handler))

(defn init-close []
  (set! (.-onbeforeunload js/window)
        (fn [_]
          (let [show-close-warning @(rf/subscribe [:sample/show-close-warning?])]
            (or show-close-warning nil)))))

(defn init-history []
  (set! (.-onpopstate js/window)
        (fn [event]
          ;(utils/log "popstate")
          ;(utils/log event)
          ;(utils/log (.-location js/document))
          ;(let [current-url (.-href (.-location js/document))]
          ;  (cond
          ;    (.endsWith current-url "/editor") (rf/dispatch [:view/editor])
          ;    (.endsWith current-url "/view") (rf/dispatch [:view/standalone])
          ;    :else (rf/dispatch [:view/editor])))
          ;(let [browser-history-index (.-state event)]
          ;  (rf/dispatch [:change-history browser-history-index]))
          (secretary.core/dispatch! (.-pathname (.-location js/document))))))

(defroute "/:url/view" [url]
          (rf/dispatch [:location-change url nil true]))

(defroute "/:url/:id/view" [url id]
          (rf/dispatch [:location-change url (int id) true]))

(defroute "/:url" [url]
          (rf/dispatch [:location-change url nil nil]))

(defroute "/:url/:id" [url id]
          (rf/dispatch [:location-change url (int id) nil]))

(defroute "/*/view"  {:as params}
          (rf/dispatch [:location-change (:* params) nil true]))

(defroute "/*"  {:as params}
          (rf/dispatch [:location-change (:* params) nil nil]))


(init)
(init-close)
(init-history)