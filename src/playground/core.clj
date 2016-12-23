(ns playground.core
  (:require [immutant.web :as web]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-toml.core :as toml]
            [clojure.walk :refer [keywordize-keys]])
  (:gen-class))

(defn handler [request]
  "Hi!")

(defroutes app
           (GET "/" [] handler)
           (route/not-found "Page not found."))

(defn -main [& args]
  (let [conf (-> (first args) slurp toml/parse-string keywordize-keys)]
    (web/run app {:port (-> conf :web :port)})))
