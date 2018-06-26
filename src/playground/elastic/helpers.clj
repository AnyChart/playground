(ns playground.elastic.helpers
  (:require [playground.elastic.consts :as elastic-consts]))


;; =====================================================================================================================
;; Helpers
;; =====================================================================================================================
(defn prepare-sample
  ([sample] (prepare-sample sample nil nil))
  ([sample repo-name version-name]
   (-> sample
       (select-keys (keys (:properties elastic-consts/mapping)))
       (update :repo-name #(or % repo-name))
       (update :version-name #(or % version-name))
       (assoc :name-kw (:name sample))
       (assoc :tags-kw (:tags sample)))))


(defn bulk-samples [samples conf]
  (mapcat (fn [sample]
            [{:index {:_index (:index conf)
                      :_type  (:type conf)}}
             sample])
          samples))
