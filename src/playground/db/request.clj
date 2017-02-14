(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]))

(defqueries "sql/queries.sql")

(defn sql-sym [sym]
  (symbol (str 'sql- (name sym))))

;generate for each request something like:
;(defn versions [db & [params]
;  (sql-versions params {:connection (:conn db)}))
(defmacro defsql [fn-name & [opts]]
  (if (ends-with? fn-name "<!")
    `(defn ~fn-name [db# & [params#]]
       (:generated_key (~(sql-sym fn-name) params# (merge {:connection (:conn db#)} ~opts))))
    `(defn ~fn-name [db# & [params#]]
       (~(sql-sym fn-name) params# (merge {:connection (:conn db#)} ~opts)))))

;; projects
(defsql projects)

(defsql add-project<!)

(defsql project-by-name)

;; versions
(defsql versions)

(defsql version-by-name {:result-set-fn first})

(defsql add-version<!)

(defsql delete-version!)

(defsql show-version!)

;; groups
(defsql add-group<!)

(defsql delete-groups!)

;; samples
(defn parse-sample [sample]
  (-> sample
      (assoc :scripts (parse-string (:scripts sample)))
      (assoc :styles (parse-string (:styles sample)))))

(defsql samples)

(defsql top-samples {:row-fn parse-sample})

(defsql sample-by-url {:result-set-fn first
                       :row-fn        parse-sample})

; TODO: wait until yesql has multiple insert
;(defsql add-samples!)

(defsql delete-samples!)

(defn add-samples! [db group-id version-id samples]
  (insert-multiple! db :samples (map (fn [sample]
                                       {:group_id          group-id
                                        :version_id        version-id

                                        :name              (:name sample)
                                        :description       (:description sample)
                                        :short_description (:short_description sample)
                                        :hidden            (:hidden sample)
                                        :url               (:url sample)

                                        :tags              (generate-string (:tags sample))
                                        :is_new            (:is_new sample)
                                        :exports           (:exports sample)
                                        :index             (:index sample)

                                        :styles            (generate-string (:styles sample))
                                        :scripts           (generate-string (:scripts sample))
                                        :local_scripts     (generate-string (:local-scripts sample))

                                        :code              (:code sample)
                                        :code_type         (:code_type sample)

                                        :markup            (:markup sample)
                                        :markup_type       (:markup_type sample)

                                        :style             (:style sample)
                                        :style_type        (:style_type sample)})
                                     samples)))