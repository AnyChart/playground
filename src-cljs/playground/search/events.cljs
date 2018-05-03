(ns playground.search.events
  (:require [re-frame.core :as rf]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))


;(rf/reg-event-fx
;  :search/search
;  (fn [{db :db} [_ q]]
;    {:search/request q}))
;
;
;(rf/reg-fx
;  :search/request
;  (fn [q]
;    (POST "/search"
;          {:params        {:q q}
;           :handler       #(rf/dispatch [:search/request-response %1])
;           :error-handler #(rf/dispatch [:search/request-error %1])})))
;;
;
;(rf/reg-event-db
;  :search/request-response
;  (fn [db [_ data]]
;    (-> db
;        (assoc-in [:search :results] data)
;        (assoc-in [:search :show] true))))
;
;
;(rf/reg-event-db
;  :search/request-error
;  (fn [db [_ error]]
;    (js/alert "Search error" error)
;    db))


(rf/reg-event-db
  :search/close
  (fn [db _]
    (assoc-in db [:search :show] false)))


(rf/reg-event-db
  :search/hide-hints
  (fn [db _]
    (-> db
        (assoc-in [:search :show] false)
        (assoc-in [:search :query-hints] []))))


(rf/reg-event-db
  :search/show-hints
  (fn [db [_ q]]
    (let [hints (take 30 (filter (fn [hint]
                                   (string/includes? (string/lower-case hint)
                                                     (string/lower-case (string/trim q))))
                                 (-> db :search :hints)))]
      (-> db
          (assoc-in [:search :query-hints] hints)
          (assoc-in [:search :show] (seq hints))))))