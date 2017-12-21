(ns playground.db.migration
  (:require [playground.db.request :as db-req]
            [taoensso.timbre :as timbre]))

(defn refresh-views-from-canonical-visits [db]
  (timbre/info "Refresh samples views")
  (let [samples (db-req/samples-latest db)]
    (doseq [sample samples]
      (db-req/update-sample-views-from-canonical-visits! db {:url     (:url sample)
                                                             :repo-id (:repo-id sample)}))
    (timbre/info "Refresh samples views end: " (count samples))
    (count samples)))