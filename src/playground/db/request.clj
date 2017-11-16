(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extra]
            [playground.utils.utils :as utils]
            [version-clj.core :as version-clj]))

;; =====================================================================================================================
;; Include sql files
;; =====================================================================================================================
(defqueries "sql/queries/samples.sql")
(defqueries "sql/queries/repos.sql")
(defqueries "sql/queries/versions.sql")
(defqueries "sql/queries/session.sql")
(defqueries "sql/queries/datasets.sql")
(defqueries "sql/queries/tags.sql")

;; =====================================================================================================================
;; Macroses and util functions
;; =====================================================================================================================
(defn underscore->dash [data]
  (kebab-extra/transform-keys kebab/->kebab-case data))

(defn dash->underscore [data]
  (kebab-extra/transform-keys kebab/->snake_case data))

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
         (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (or (:conn db#) db#)} ~opts))))
    `(defn ~fn-name [db# & [params#]]
       (~(sql-sym fn-name) (dash->underscore params#) (merge {:connection (or (:conn db#) db#)} ~opts)))))

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
           (~fn-name (dash->underscore params#) (merge {:connection (or (:conn db#) db#)} ~opts))))
      `(fn [db# & [params#]]
         (~fn-name (dash->underscore params#) (merge {:connection (or (:conn db#) db#)} ~opts))))))

; TODO: maybe do something linke this:
;(db-req/transaction (get-db request)
;                    (db-req/update-sample-views! {:id (:id sample)})
;                    (db-req/update-sample-views! {:id (:id sample)})
;                    (throw (Exception. "my ex"))
;                    (db-req/update-sample-views! {:id (:id sample)}))

;(defmacro transaction [db & body]
;  (jdbc/with-db-transaction [conn (:db-spec (get-db request))]
;                            (prn "CREATED conn: " conn)
;                            )
;  )

;; =====================================================================================================================
;; Repos
;; =====================================================================================================================
(def add-repo<! (sql {:name sql-add-repo<!}))

(def repos (sql {:name   sql-repos
                 :row-fn underscore->dash}))

(def repo-by-name (sql {:name          sql-repo-by-name
                        :result-set-fn first
                        :row-fn        underscore->dash}))

;; =====================================================================================================================
;; Versions
;; =====================================================================================================================
(defn parse-version [version]
  (update version :config parse-string true))

(def versions (sql {:name          sql-versions
                    :row-fn        underscore->dash
                    :result-set-fn (fn [versions]
                                     (sort (comp - #(version-clj/version-compare (:name %1) (:name %2))) versions))}))

(def versions-repos (sql {:name   sql-versions-repos
                          :row-fn underscore->dash}))

(def version-by-name (sql {:name          sql-version-by-name
                           :result-set-fn first
                           :row-fn        parse-version}))

(def add-version<! (sql {:name sql-add-version<!}))

(def delete-version! (sql {:name sql-delete-version!}))

(def show-version! (sql {:name sql-show-version!}))

(defn last-version [db data]
  (let [versions (versions db data)
        last-version (first (sort (comp - #(version-clj/version-compare (:name %1) (:name %2)))
                                  versions))]
    last-version))

;; =====================================================================================================================
;; Samples
;; =====================================================================================================================
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

(def add-sample<! (sql {:name sql-add-sample<!}))

(def samples (sql {:name   sql-samples
                   :row-fn underscore->dash}))

(def samples-by-ids (sql {:name   sql-samples-by-ids
                          :row-fn parse-sample}))

(def sample-version (sql {:name          sql-sample-version
                          :result-set-fn (comp :version first)
                          :row-fn        underscore->dash}))

(def top-samples (sql {:name   sql-top-samples
                       :row-fn parse-sample}))

(def samples-by-version (sql {:name   sql-samples-by-version
                              :row-fn parse-sample}))

(def sample-by-url (sql {:name          sql-sample-by-url
                         :result-set-fn first
                         :row-fn        parse-sample}))

(def sample-by-hash (sql {:name          sql-sample-by-hash
                          :result-set-fn first
                          :row-fn        parse-sample}))

(def last-sample-by-hash (sql {:name          sql-last-sample-by-hash
                               :result-set-fn first
                               :row-fn        parse-sample}))

(def sample-template-by-url (sql {:name          sql-sample-template-by-url
                                  :result-set-fn first
                                  :row-fn        parse-sample}))

; TODO: wait until yesql has multiple insert
;(defsql add-samples!)

(def delete-samples! (sql {:name sql-delete-samples!}))

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

(def delete-samples-by-ids! (sql {:name sql-delete-samples-by-ids!}))

(def update-sample-views! (sql {:name sql-update-sample-views!}))

(def update-samples-preview! (sql {:name sql-update-samples-preview!}))

(def user-samples-without-preview (sql {:name sql-user-samples-without-preview}))

(def repo-samples-without-preview (sql {:name sql-repo-samples-without-preview}))


;;======================================================================================================================
;; Visits and likes
;;======================================================================================================================
(def get-visit (sql {:name          sql-get-visit
                     :result-set-fn first
                     :row-fn        underscore->dash}))

(def visit! (sql {:name sql-visit!}))

(def delete-version-visits! (sql {:name sql-delete-version-visits!}))

(def delete-repo-visits! (sql {:name sql-delete-repo-visits!}))

;;======================================================================================================================
;; Set sample latest
;;======================================================================================================================
(def update-all-samples-latest! (sql {:name sql-update-all-samples-latest!}))

(def update-version-samples-latest! (sql {:name sql-update-version-samples-latest!}))

(def update-all-user-samples-latest! (sql {:name sql-update-all-user-samples-latest!}))

(def update-version-user-samples-latest! (sql {:name sql-update-version-user-samples-latest!}))

;;======================================================================================================================
;; Templates
;;======================================================================================================================
(def template-by-url (sql {:name          sql-template-by-url
                           :result-set-fn first
                           :row-fn        parse-sample}))

(def templates (sql {:name   sql-templates
                     :row-fn parse-sample}))

(def templates-sample-ids (sql {:name   sql-templates-sample-ids
                                :row-fn :sample_id}))

(def delete-templates! (sql {:name sql-delete-templates!}))

(defn add-templates! [db ids]
  (insert-multiple! db :templates (map (fn [id] {:sample_id id}) ids)))

;;======================================================================================================================
;; Users
;;======================================================================================================================
(def add-user<! (sql {:name sql-add-user<!}))

(def get-user-by-username-or-email (sql {:name          sql-get-user-by-username-or-email
                                         :result-set-fn first}))

(def get-user-by-username (sql {:name          sql-get-user-by-username
                                :result-set-fn first}))

(def get-user-by-email (sql {:name          sql-get-user-by-email
                             :result-set-fn first}))

(def delete-user! (sql {:name sql-delete-user!}))

;;======================================================================================================================
;; Sessions
;;======================================================================================================================
(def get-session (sql {:name          sql-get-session
                       :result-set-fn first}))

(def add-session<! (sql {:name sql-add-session<!}))

(def delete-session! (sql {:name sql-delete-session!}))

;;======================================================================================================================
;; Tags
;;======================================================================================================================
(def tags (sql {:name sql-tags}))

(def top-tags (sql {:name sql-top-tags}))

(def samples-by-tag (sql {:name   sql-samples-by-tag
                          :row-fn parse-sample}))

;;======================================================================================================================
;; Datasets, datasources
;;======================================================================================================================
(defn parse-data-set [data-set]
  (-> data-set
      (assoc :tags (parse-string (:tags data-set)))
      ;(assoc :data (parse-string (:data data-set)))
      underscore->dash))

(def add-data-source<! (sql {:name sql-add-data-source<!}))

(def add-data-set<! (sql {:name sql-add-data-set<!}))

(def delete-data-sources! (sql {:name sql-delete-data-sources!}))

(def delete-data-sets! (sql {:name sql-delete-data-sets!}))

(def data-sets (sql {:name   sql-data-sets
                     :row-fn parse-data-set}))

(def top-data-sets (sql {:name   sql-top-data-sets
                         :row-fn parse-data-set}))

(def data-set-by-name (sql {:name          sql-data-set-by-name
                            :result-set-fn first
                            :row-fn        parse-data-set}))

(def data-sources (sql {:name sql-data-sources}))

;;======================================================================================================================
;; Delete all repo
;;======================================================================================================================
(def delete-samples-by-repo-name! (sql {:name sql-delete-samples-by-repo-name!}))

(def delete-versions-by-repo-name! (sql {:name sql-delete-versions-by-repo-name!}))

(def delete-repo-by-name! (sql {:name sql-delete-repo-by-name!}))

;;======================================================================================================================
;; Landing
;;======================================================================================================================
(defn top-tags-samples-transformer [res]
  (doall (distinct (map #(dissoc % :tag-count) res))))
(def top-tags-samples (sql {:name          sql-top-tags-samples
                            :result-set-fn top-tags-samples-transformer
                            :row-fn        parse-sample}))
(defn get-top-tags-samples [db {:keys [offset count]}]
  (let [samples (top-tags-samples db)]
    (take count (drop offset samples))))


;;======================================================================================================================
;; Sitemap
;;======================================================================================================================
(def sitemap-sample-urls (sql {:name   sql-sitemap-sample-urls
                               :row-fn underscore->dash}))