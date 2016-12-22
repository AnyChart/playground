(ns playground.core
  (:require [immutant.web :as web]
            [compojure.core :refer :all]
            [compojure.route :as route])
  (:gen-class))

(defn handler [request]
  "Hi!")

(defroutes app
           (GET "/" [] handler)
           (route/not-found "Page not found."))

(defn -main []
  (web/run app))
