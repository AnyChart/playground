(ns playground.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.subs]
            [playground.events]
            [playground.views :as views]
            [playground.js]
            [playground.editors.tern :as tern]
            [cognitect.transit :as t]
    ;[accountant.core :as accountant]
            ))

;(accountant/configure-navigation! {:nav-handler
;                                   (fn [path] (utils/log "Nav-handler: " path))
;                                   :path-exists?
;                                   (fn [path] (utils/log "Path exist? " path))})

(enable-console-print!)


(defn init [data]
  (rf/dispatch-sync [:init data]))


(defn mount-html []
  (reagent/render-component [views/app] (.getElementById js/document "main-container")))


(defn ^:export run [data]
  (let [r (t/reader :json)
        data (t/read r data)]
    ;(utils/log "Data: " data)
    (init data)
    (mount-html)
    (tern/init-tern)))