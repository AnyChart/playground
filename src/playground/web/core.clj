(ns playground.web.core
  (:require [immutant.web :as web]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [toml.core :as toml]
            [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as component]
            [playground.web.routes :refer [app-routes]]))

(defn- component-middleware [web-component handler]
  (fn [request]
    (handler (assoc request :component web-component))))

(defn create-web-handler [web-component]
  (component-middleware web-component app-routes))

(defrecord Web [server conf db]
  component/Lifecycle

  (start [component]
    (timbre/info "Web start")
    (assoc component :server (web/run
                               (create-web-handler component)
                               conf)))

  (stop [component]
    (timbre/info "Web stop")
    (web/stop (:server component))
    (assoc component :server nil)))

(defn new-web [conf]
  (map->Web {:conf conf}))