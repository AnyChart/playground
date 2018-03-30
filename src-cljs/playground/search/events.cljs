(ns playground.search.events
  (:require [re-frame.core :as rf]
            [ajax.core :refer [GET POST]]))


(rf/reg-event-fx
  :search/search
  (fn [{db :db} [_ q]]
    {:search/request q}))


(rf/reg-fx
  :search/request
  (fn [q]
    (POST "/search"
          {:params        {:q q}
           :handler       #(rf/dispatch [:search/request-response %1])
           :error-handler #(rf/dispatch [:search/request-error %1])})))
;

(rf/reg-event-db
  :search/request-response
  (fn [db [_ data]]
    (-> db
        (assoc-in [:search :results] data)
        (assoc-in [:search :show] true))))


(rf/reg-event-db
  :search/request-error
  (fn [db [_ error]]
    (js/alert "Search error" error)
    db))


(rf/reg-event-db
  :search/close
  (fn [db _]
    (assoc-in db [:search :show] false)))
