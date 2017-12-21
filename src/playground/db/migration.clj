(ns playground.db.migration
  (:require [playground.db.request :as db-req]))

(defn refresh-views-from-canonical-visits [db]
  (let [samples (db-req/samples-latest db)]
    (doseq [sample samples]
      (db-req/update-sample-views-from-canonical-visits! db {:url     (:url sample)
                                                             :repo-id (:repo-id sample)}))
    (count samples)))