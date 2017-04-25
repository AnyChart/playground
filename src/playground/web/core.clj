(ns playground.web.core
  (:require [immutant.web :as web]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [toml.core :as toml]
            [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as component]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.format-params :refer [wrap-transit-json-params]]
            [ring.middleware.format-response :refer [wrap-transit-json-response]]
            [playground.web.routes :refer [app-routes]]))

(defn- component-middleware [web-component handler]
  (fn [request]
    (handler (assoc request :component web-component))))

(defn create-web-handler [web-component]
  (component-middleware web-component #'app-routes))

(defrecord Web [server conf db]
  component/Lifecycle

  (start [component]
    (timbre/info "Web start" conf)
    (assoc component :server (web/run
                               (-> (create-web-handler component)
                                   wrap-transit-json-params
                                   wrap-transit-json-response
                                   wrap-keyword-params
                                   wrap-params)
                               {:port (:port conf)})))

  (stop [component]
    (timbre/info "Web stop")
    (web/stop (:server component))
    (assoc component :server nil)))

(defn new-web [conf]
  (map->Web {:conf conf}))