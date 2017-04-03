(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]
            [wharf.core :as wharf]))

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

;; repos
(defsql add-repo<!)

(defsql repos)

(defsql repo-by-name {:result-set-fn first})

;; versions
(defn parse-version [version]
  (update version :config parse-string true))

(defsql versions)

(defsql version-by-name {:result-set-fn first
                         :row-fn        parse-version})

(defsql add-version<!)

(defsql delete-version!)

(defsql show-version!)

;; samples
(defn parse-sample [sample]
  ;; TODO: rename all keywords with underscore to dash
  ;(wharf/transform-keys wharf/underscore->hyphen
  (-> sample
      (assoc :tags (parse-string (:tags sample)))
      (assoc :scripts (parse-string (:scripts sample)))
      (assoc :styles (parse-string (:styles sample))))
  ;)
  )

(defsql add-sample<!)

(defsql samples)

(defsql sample-version {:result-set-fn (comp :version first)})

(defsql top-samples {:row-fn parse-sample})

(defsql sample-by-url {:result-set-fn first
                       :row-fn        parse-sample})

(defsql sample-by-hash {:result-set-fn first
                        :row-fn        parse-sample})

; TODO: wait until yesql has multiple insert
;(defsql add-samples!)

(defsql delete-samples!)

(defn- insert-sample [sample & [version-id]]
  {:version_id        version-id

   :name              (:name sample)
   :description       (:description sample)
   :short_description (:short_description sample)

   :url               (:url sample)
   :show_on_landing   (:show_on_landing sample)
   :tags              (generate-string (:tags sample))
   :exports           (:exports sample)

   :styles            (when (seq (:styles sample))
                        (generate-string (:styles sample)))

   :scripts           (when (seq (:scripts sample))
                        (generate-string (:scripts sample)))

   :local_scripts     (when (seq (:local-scripts sample))
                        (generate-string (:local-scripts sample)))

   :code              (:code sample)
   :code_type         (:code_type sample)

   :markup            (:markup sample)
   :markup_type       (:markup_type sample)

   :style             (:style sample)
   :style_type        (:style_type sample)

   :version           (or (:version sample) 0)})

(defn add-sample! [db sample]
  (add-sample<! db (insert-sample sample)))

(defn add-samples! [db version-id samples]
  (insert-multiple! db :samples (map #(insert-sample % version-id) samples)))