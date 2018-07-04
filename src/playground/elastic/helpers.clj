(ns playground.elastic.helpers
  (:require [playground.elastic.consts :as elastic-consts]))


;; =====================================================================================================================
;; Helpers
;; =====================================================================================================================
(defn prepare-sample
  ([sample] (prepare-sample sample nil nil nil))
  ([sample repo-name version-name version-id]
   (-> sample
       (select-keys (keys (:properties elastic-consts/mapping)))
       (update :repo-name #(or % repo-name))
       (update :version-name #(or % version-name))
       (update :version-id #(or % version-id))
       (assoc :name-kw (:name sample))
       (assoc :tags-kw (:tags sample)))))


(defn bulk-samples [samples conf]
  (mapcat (fn [sample]
            [{:index {:_index (:index conf)
                      :_type  (:type conf)}}
             sample])
          samples))
