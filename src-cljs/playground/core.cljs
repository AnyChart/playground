(ns playground.core
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.subs]
            [playground.events]
            [playground.utils :as utils]
            [playground.views :as views]
            [cognitect.transit :as t]))

(defn pre-init [data]
  (rf/dispatch-sync [:pre-init data]))

(defn post-init [data]
  (rf/dispatch-sync [:init data]))

(defn mount-html []
  (reagent/render-component [views/app] (.getElementById js/document "main-container")))

(defn ^:export run [data]
  (let [r (t/reader :json)
        data (t/read r data)]
    ;(utils/log "Data: " data)
    (pre-init data)
    (mount-html)
    (post-init data)))
