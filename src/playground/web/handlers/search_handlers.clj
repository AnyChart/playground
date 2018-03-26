(ns playground.web.handlers.search-handlers
  (:require [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
            [playground.db.elastic :as elastic]
            [taoensso.timbre :as timbre]))


(defn search [request]
  (timbre/info (-> request :params))
  (let [q (-> request :params :q)
        results (elastic/search q (-> (get-db request) :config :elastic))]
    (response results)))