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
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [redirect]]
            [playground.web.routes :refer [app-routes]]
            [playground.web.sessions :as session]
            [clojure.string :as string]))

(defn- component-middleware [web-component handler]
  (fn [request]
    (handler (assoc request :component web-component))))

(defn create-web-handler [web-component]
  (component-middleware web-component #'app-routes))


(defn create-redirect-wrapper [handler conf]
  (let [redirects (:redirect-replacements conf)]
    (fn [request]
      (let [uri (:uri request)
            new-uri (reduce
                      (fn [new-uri redirect]
                        (if (string/includes? new-uri (:from redirect))
                          (string/replace new-uri
                                          (re-pattern (:from redirect))
                                          (:to redirect))
                          new-uri))
                      uri
                      redirects)]
        (if (not= uri new-uri)
          (redirect new-uri 301)
          (handler request))))))

;; TODO: delete after 6-9 months
(defn wrap-api-redirects [handler]
  (fn [request]
    (if (and (string/starts-with? (:uri request) "/api/")
             (not (string/includes? (:uri request) "/_samples/")))
      (let [parts (string/split (:uri request) #"/")]
        (redirect (string/join "/" (concat (butlast parts) ["_samples"] [(last parts)])) 301))
      (handler request))))


(defrecord Web [server conf db]
  component/Lifecycle

  (start [component]
    (timbre/info "Web start" conf)
    (assoc component :server (web/run
                               (-> (create-web-handler component)
                                   (create-redirect-wrapper conf)
                                   wrap-api-redirects
                                   wrap-transit-json-params
                                   wrap-transit-json-response
                                   wrap-keyword-params
                                   wrap-params
                                   (wrap-session {:store (session/create-storage db)}))
                               {:port (:port conf)})))

  (stop [component]
    (timbre/info "Web stop")
    (web/stop (:server component))
    (assoc component :server nil)))

(defn new-web [conf]
  (map->Web {:conf conf}))