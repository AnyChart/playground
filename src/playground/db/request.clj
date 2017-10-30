(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extra]
            [playground.utils.utils :as utils]
            [version-clj.core :as version-clj]))

(defn underscore->dash [data]
  (kebab-extra/transform-keys kebab/->kebab-case data))

(defn dash->underscore [data]
  (kebab-extra/transform-keys kebab/->snake_case data))

(defqueries "sql/queries/samples.sql")
(defqueries "sql/queries/repos.sql")
(defqueries "sql/queries/versions.sql")
(defqueries "sql/queries/session.sql")
(defqueries "sql/queries/datasets.sql")
(defqueries "sql/queries/tags.sql")

(defn sql-sym [sym]
  (symbol (str 'sql- (name sym))))

(defmacro defsql
  "generate for each request something like:
  (defn versions [db & [params]
   (sql-versions params {:connection (:conn db)}))"
  [fn-name & [opts]]
  (if (ends-with? fn-name "<!")
    `(defn ~fn-name [db# & [params#]]
       (:generated_key
         (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (:conn db#)} ~opts))))
    `(defn ~fn-name [db# & [params#]]
       (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (:conn db#)} ~opts)))))

(defmacro sql
  "generate for each request something like:
  (defn versions [db & [params]
   (sql-versions params {:connection (:conn db)}))"
  [opts]
  (let [fn-name (:name opts)
        opts (dissoc opts :name)]
    (if (ends-with? fn-name "<!")
     `(fn [db# & [params#]]
        (:generated_key
          (~fn-name (dash->underscore params#) (merge {:connection (:conn db#)} ~opts))))
     `(fn [db# & [params#]]
        (~fn-name (dash->underscore params#) (merge {:connection (:conn db#)} ~opts))))))

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

(defn last-version [db data]
  (let [versions (versions db data)
        last-version (first (sort (comp - #(version-clj/version-compare (:name %1) (:name %2)))
                                  versions))]
    last-version))

;; samples
(defn add-full-url [sample]
  (assoc sample :full-url (utils/sample-url sample)))

(defn parse-sample [sample]
  (-> sample
      (assoc :tags (parse-string (:tags sample)))
      (assoc :deleted-tags (parse-string (:deleted_tags sample)))
      (assoc :scripts (parse-string (:scripts sample)))
      (assoc :styles (parse-string (:styles sample)))
      underscore->dash
      add-full-url))

(defsql add-sample<!)
(defsql samples {:row-fn underscore->dash})
(defsql samples-by-ids {:row-fn parse-sample})
(defsql sample-version {:result-set-fn (comp :version first)
                        :row-fn        underscore->dash})
(defsql top-samples {:row-fn parse-sample})
(defsql samples-by-version {:row-fn parse-sample})
(defsql sample-by-url {:result-set-fn first
                       :row-fn        parse-sample})
(defsql sample-by-hash {:result-set-fn first
                        :row-fn        parse-sample})
(defsql last-sample-by-hash {:result-set-fn first
                             :row-fn        parse-sample})
(defsql sample-template-by-url {:result-set-fn first
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
   :tags              (generate-string (:tags sample))
   :deleted_tags      (generate-string (:deleted-tags sample))
   :exports           (:exports sample)

   :owner_id          (:owner-id sample)

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
  (doall (map :generated_key
              (insert-multiple! db :samples (map #(insert-sample % version-id) samples)))))

(defsql delete-samples-by-ids!)
(defsql update-sample-views!)
(defsql update-samples-preview!)
(defsql user-samples-without-preview)
(defsql repo-samples-without-preview)

;; set sample latest
(defsql update-all-samples-latest!)
(defsql update-version-samples-latest!)
(defsql update-all-user-samples-latest!)
(defsql update-version-user-samples-latest!)


;; templates
(defsql template-by-url {:result-set-fn first
                         :row-fn        parse-sample})
(defsql templates {:row-fn parse-sample})
(defsql templates-sample-ids {:row-fn :sample_id})
(defsql delete-templates!)

(defn add-templates! [db ids]
  (insert-multiple! db :templates (map (fn [id] {:sample_id id}) ids)))

;; users
(defsql add-user<!)
(defsql get-user-by-username-or-email {:result-set-fn first})
(defsql get-user-by-username {:result-set-fn first})
(defsql get-user-by-email {:result-set-fn first})
(defsql delete-user!)

;; sessions
(defsql get-session {:result-set-fn first})
(defsql add-session<!)
(defsql delete-session!)

;; tags
(defsql tags)
(defsql top-tags)
(defsql samples-by-tag {:row-fn parse-sample})

;;data sources
(defn parse-data-set [data-set]
  (-> data-set
      (assoc :tags (parse-string (:tags data-set)))
      ;(assoc :data (parse-string (:data data-set)))
      underscore->dash))

(defsql add-data-source<!)
(defsql add-data-set<!)
(defsql delete-data-sources!)
(defsql delete-data-sets!)
(defsql data-sets {:row-fn parse-data-set})
(defsql top-data-sets {:row-fn parse-data-set})
(defsql data-set-by-name {:result-set-fn first
                          :row-fn        parse-data-set})
(defsql data-sources)


;; delete all repo
(defsql delete-samples-by-repo-name!)
(defsql delete-versions-by-repo-name!)
(defsql delete-repo-by-name!)

;; landing
(defn top-tags-samples-transformer [res]
  (doall (distinct (map #(dissoc % :tag-count) res))))

(defsql top-tags-samples {:result-set-fn top-tags-samples-transformer
                          :row-fn        parse-sample})

(defn get-top-tags-samples [db {:keys [offset count]}]
  (let [samples (top-tags-samples db)]
    (take count (drop offset samples))))

;(defsql sitemap-sample-urls {:row-fn underscore->dash})
(def sitemap-sample-urls (sql {:name sql-sitemap-sample-urls
                               :row-fn underscore->dash}))