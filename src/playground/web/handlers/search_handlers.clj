(ns playground.web.handlers.search-handlers
  (:require [playground.web.helpers :refer :all]
            [playground.web.handlers.constants :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
            [playground.db.elastic :as elastic]
            [taoensso.timbre :as timbre]
            [playground.db.request :as db-req]
            [playground.views.search.search-page :as search-view]))


(defn search-page [request]
  (let [q (-> request :params :q)
        page (get-pagination request)
        result (elastic/search (-> (get-db request) :config :elastic)
                               q
                               (* samples-per-page page)
                               samples-per-page)
        total (:total result)
        end (>= (* (inc page) samples-per-page) total)]
    (search-view/page (merge (get-app-data request)
                             {:q      q
                              :page   page
                              :end    end
                              :result result}))))


(defn search [request]
  (let [q (-> request :params :q)
        offset (or (-> request :params :offset) 0)
        result (elastic/search (-> (get-db request) :config :elastic)
                               q
                               offset
                               samples-per-page)
        end (<= (- (:total result) offset) samples-per-page)
        result (assoc result :end end)]
    (response result)))