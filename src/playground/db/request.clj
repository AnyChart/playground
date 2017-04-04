(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extra]))

(defn underscore->dash [data]
  (kebab-extra/transform-keys kebab/->kebab-case data))

(defn dash->underscore [data]
  (kebab-extra/transform-keys kebab/->snake_case data))

(defqueries "sql/queries.sql")

(defn sql-sym [sym]
  (symbol (str 'sql- (name sym))))

;generate for each request something like:
;(defn versions [db & [params]
;  (sql-versions params {:connection (:conn db)}))
(defmacro defsql [fn-name & [opts]]
  (if (ends-with? fn-name "<!")
    `(defn ~fn-name [db# & [params#]]
       (:generated_key (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (:conn db#)} ~opts))))
    `(defn ~fn-name [db# & [params#]]
       (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (:conn db#)} ~opts)))))

;; repos
(defsql add-repo<!)

(defsql repos {:row-fn underscore->dash})

(defsql repo-by-name {:result-set-fn first
                      :row-fn        underscore->dash})

;; versions
(defn parse-version [version]
  (update version :config parse-string true))

(defsql versions {:row-fn underscore->dash})

(defsql version-by-name {:result-set-fn first
                         :row-fn        parse-version})

(defsql add-version<!)

(defsql delete-version!)

(defsql show-version!)

;; samples
(defn parse-sample [sample]
  ;; TODO: rename all keywords with underscore to dash
  (-> sample
      (assoc :tags (parse-string (:tags sample)))
      (assoc :scripts (parse-string (:scripts sample)))
      (assoc :styles (parse-string (:styles sample)))
      underscore->dash))

(defsql add-sample<!)

(defsql samples {:row-fn underscore->dash})

(defsql sample-version {:result-set-fn (comp :version first)
                        :row-fn        underscore->dash})

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
   :short_description (:short-description sample)

   :url               (:url sample)
   :show_on_landing   (:show-on-landing sample)
   :tags              (generate-string (:tags sample))
   :exports           (:exports sample)

   :styles            (when (seq (:styles sample))
                        (generate-string (:styles sample)))

   :scripts           (when (seq (:scripts sample))
                        (generate-string (:scripts sample)))

   :local_scripts     (when (seq (:local-scripts sample))
                        (generate-string (:local-scripts sample)))

   :code              (:code sample)
   :code_type         (:code-type sample)

   :markup            (:markup sample)
   :markup_type       (:markup-type sample)

   :style             (:style sample)
   :style_type        (:style-type sample)

   :version           (or (:version sample) 0)})

(defn add-sample! [db sample]
  (add-sample<! db (insert-sample sample)))

(defn add-samples! [db version-id samples]
  (insert-multiple! db :samples (map #(insert-sample % version-id) samples)))