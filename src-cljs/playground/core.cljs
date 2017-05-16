(ns playground.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.subs]
            [playground.events]
            [playground.utils :as utils]
            [playground.views :as views]
            [cognitect.transit :as t]
    ;[accountant.core :as accountant]
            ))

;(accountant/configure-navigation! {:nav-handler
;                                   (fn [path] (utils/log "Nav-handler: " path))
;                                   :path-exists?
;                                   (fn [path] (utils/log "Path exist? " path))})

(defn init [data]
  (rf/dispatch-sync [:pre-init data]))

(defn mount-html []
  (reagent/render-component [views/app] (.getElementById js/document "main-container")))

(defn ^:export run [data]
  (let [r (t/reader :json)
        data (t/read r data)]
    ;(utils/log "Data: " data)
    (init data)
    (mount-html)))
