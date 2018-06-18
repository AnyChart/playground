(ns playground.web.handlers.search-handlers
  (:require [playground.web.helpers :refer :all]
            [playground.web.handlers.constants :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
            [playground.db.elastic :as elastic]
            [taoensso.timbre :as timbre]
            [playground.db.request :as db-req]
            [playground.views.search.search-page :as search-view]
            [clojure.string :as string]))


(defn search-page [request]
  (let [q (-> request :params :q)
        page (get-pagination request)
        result (elastic/search (-> (get-db request) :config :elastic)
                               q
                               (* samples-per-page page)
                               samples-per-page)]
    (search-view/page (merge (get-app-data request)
                             {:q      q
                              :page   page
                              :result result}))))


(defn search [request]
  (let [q (-> request :params :q)
        offset (or (-> request :params :offset) 0)
        result (elastic/search (-> (get-db request) :config :elastic)
                               q
                               offset
                               samples-per-page)]
    (response result)))


(defn search-hints [request]
  (response (:all-tags (get-app-data request))))


;(defn search-hints-by-query [request]
;  (let [q (-> request :params :q)
;        hints (sort (map :name (:all-tags (get-app-data request))))
;        hints (take 30 (filter (fn [hint]
;                                 (string/includes? (string/lower-case hint)
;                                                   (string/lower-case (string/trim q))))
;                               hints))]
;    (response hints)))