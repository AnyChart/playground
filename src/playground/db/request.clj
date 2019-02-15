(ns playground.db.request
  (:require [yesql.core :refer [defqueries]]
            [clojure.string :refer [ends-with?]]
            [playground.db.core :refer [insert-multiple!]]
            [cheshire.core :refer [generate-string parse-string]]
            [camel-snake-kebab.core :as kebab]
            [camel-snake-kebab.extras :as kebab-extra]
            [playground.utils.utils :as utils]
            [clojure.java.jdbc :as jdbc]
            [mpg.core :as mpg]))


(mpg/patch {:datetime false})

;; =====================================================================================================================
;; Include sql files
;; =====================================================================================================================
(defqueries "sql/queries/samples.sql")
(defqueries "sql/queries/repos.sql")
(defqueries "sql/queries/versions.sql")
(defqueries "sql/queries/session.sql")
(defqueries "sql/queries/datasets.sql")
(defqueries "sql/queries/tags.sql")
(defqueries "sql/queries/visits.sql")

;; =====================================================================================================================
;; Macroses and util functions
;; =====================================================================================================================
(defn underscore->dash [data]
  (kebab-extra/transform-keys kebab/->kebab-case data))


(defn dash->underscore [data]
  (kebab-extra/transform-keys kebab/->snake_case data))


(defn vec->arr [array-vector db]
  (let [conn (jdbc/get-connection (or (:conn db) db))
        arr (.createArrayOf conn
                            "varchar"
                            (into-array String array-vector))]
    (when (:conn db)
      (.close conn))
    arr))


(defn pg-params [data db]
  (reduce-kv (fn [res key val]
               (assoc res key
                          (if (and (sequential? val)
                                   (not (:arr-ignore (meta val))))
                            (vec->arr val db)
                            val)))
             {}
             data))


(defn raw-coll
  "yesql transforms vector or list in 'IN' clause ( ... WHERE id IN (:ids)...
  to appropriate string  (... WHERE id IN (1,2,3) ... ), but sometimes we need to insert vec or list
  to field with type like ( varchar(10)[] ) - for this we use (pg-params) function that by default
  transform vecs and lists without :arr-ignore meta to appropriate database array.
  So we need to add :arr-ingore meta tag if we want to use vec or list in IN clause"
  [data]
  (with-meta data {:arr-ignore true}))


;(defn sql-sym [sym]
;  (symbol (str 'sql- (name sym))))


;(defmacro defsql
;  "generate for each request something like:
;  (defn versions [db & [params]
;   (sql-versions params {:connection (:conn db)}))"
;  [fn-name & [opts]]
;  (if (ends-with? fn-name "<!")
;    `(defn ~fn-name [db# & [params#]]
;       (:generated_key
;         (~(sql-sym fn-name) (pg-params (dash->underscore params#) db#) (merge {:connection (or (:conn db#) db#)} ~opts))))
;    `(defn ~fn-name [db# & [params#]]
;       (~(sql-sym fn-name) (pg-params (dash->underscore params#) db#) (merge {:connection (or (:conn db#) db#)} ~opts)))))


(defmacro sql
  "generate for each request something like:
  (defn versions [db & [params]
   (sql-versions params {:connection (:conn db)}))"
  [opts]
  (let [fn-name (:name opts)
        return-whole-row (:return-whole-row opts)
        opts (dissoc opts :name :return-whole-row)]
    (if (ends-with? fn-name "<!")
      `(fn [db# & [params#]]
         (~(if return-whole-row
             underscore->dash
             :id)
           (~fn-name (dash->underscore (pg-params params# db#)) (merge {:connection (or (:conn db#) db#)} ~opts))))
      `(fn [db# & [params#]]
         (~fn-name (dash->underscore (pg-params params# db#)) (merge {:connection (or (:conn db#) db#)} ~opts))))))


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

(def repo-update-actual-versions! (sql {:name sql-repo-update-actual-versions!}))


;; =====================================================================================================================
;; Versions
;; =====================================================================================================================
;(defn parse-version [version]
;  (update version :config parse-string true))

(def versions (sql {:name          sql-versions
                    :row-fn        underscore->dash
                    :result-set-fn (fn [versions]
                                     (utils/sort-versions :name versions))}))


(def versions-by-repo-name (sql {:name          sql-versions-by-repo-name
                                 :row-fn        underscore->dash
                                 :result-set-fn (fn [versions]
                                                  (->> versions
                                                       (map :name)
                                                       utils/sort-versions))}))


(def versions-by-repos-names (sql {:name          sql-versions-by-repos-names
                                   :row-fn        underscore->dash
                                   :result-set-fn (fn [versions]
                                                    (->> versions
                                                         (map :name)
                                                         utils/sort-versions))}))


(def versions-repos (sql {:name   sql-versions-repos
                          :row-fn underscore->dash}))

(def version-by-name (sql {:name          sql-version-by-name
                           :result-set-fn first
                           ;:row-fn        parse-version
                           }))

(def add-version<! (sql {:name sql-add-version<!}))

(def delete-version! (sql {:name sql-delete-version!}))

(def show-version! (sql {:name sql-show-version!}))

(defn last-version [db data]
  (let [versions (versions db data)
        last-version (first (utils/sort-versions :name versions))]
    last-version))

;; =====================================================================================================================
;; Samples
;; =====================================================================================================================
(defn add-full-url [sample]
  (assoc sample :full-url (utils/sample-url sample)))

(defn parse-sample [sample]
  (-> sample
      ;(assoc :tags (parse-string (:tags sample)))
      ;(assoc :deleted-tags (parse-string (:deleted_tags sample)))
      ;(assoc :scripts (parse-string (:scripts sample)))
      ;(assoc :styles (parse-string (:styles sample)))
      underscore->dash
      add-full-url))

(def url-exist (sql {:name          sql-url-exist
                     :result-set-fn first}))

(def add-sample<! (sql {:name             sql-add-sample<!
                        :return-whole-row true}))

(def samples (sql {:name   sql-samples
                   :row-fn underscore->dash}))

(def samples-latest (sql {:name   sql-samples-latest
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

(def samples-by-user (sql {:name  sql-samples-by-user 
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
   :tags              (vec (:tags sample))
   :deleted_tags      (vec (:deleted-tags sample))

   :owner_id          (:owner-id sample)

   :styles            (vec (:styles sample))
   :scripts           (vec (:scripts sample))

   :code              (:code sample)
   :code_type         (:code-type sample)

   :markup            (:markup sample)
   :markup_type       (:markup-type sample)

   :style             (:style sample)
   :style_type        (:style-type sample)

   :version           (or (:version sample) 0)})

(defn add-sample! [db sample]
  (let [s (insert-sample sample)]
    (add-sample<! db s)))

(defn add-samples! [db version-id samples]
  (doall (map :id                                           ; :generated-key for MySQL
              (insert-multiple! db :samples (map #(insert-sample % version-id) samples)))))

(def update-samples-preview! (sql {:name sql-update-samples-preview!}))

(def user-samples-without-preview (sql {:name sql-user-samples-without-preview}))

(def repo-samples-without-preview (sql {:name sql-repo-samples-without-preview}))

(def group-samples (sql {:name sql-group-samples}))

;;======================================================================================================================
;; For Elastic
;;======================================================================================================================
(def search-samples (sql {:name   sql-search-samples
                          :row-fn parse-sample}))

(def elastic-samples-version (sql {:name   sql-elastic-samples-version
                                   :row-fn parse-sample}))

;;======================================================================================================================
;; Visits and likes
;;======================================================================================================================
(def get-visit (sql {:name          sql-get-visit
                     :result-set-fn first
                     :row-fn        underscore->dash}))

(def visit! (sql {:name sql-visit!}))

(def delete-version-visits! (sql {:name sql-delete-version-visits!}))

(def delete-repo-visits! (sql {:name sql-delete-repo-visits!}))

;; canonical visits
(def get-canonical-visit (sql {:name          sql-get-canonical-visit
                               :result-set-fn first
                               :row-fn        underscore->dash}))

(def canonical-visit! (sql {:name sql-canonical-visit!}))

(def update-sample-views-from-canonical-visits! (sql {:name sql-update-sample-views-from-canonical-visits!}))

;;======================================================================================================================
;; Set sample latest
;;======================================================================================================================
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

(def get-user-by-uid (sql {:name          sql-get-user-by-uid
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

(def tag-name-by-id (sql {:name          sql-tag-name-by-id
                          :row-fn        :name
                          :result-set-fn first}))

(def update-tags-mw! (sql {:name sql-update-tags!}))

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
;(defn top-tags-samples-transformer [res]
;  (doall (distinct (map #(dissoc % :tag-count) res))))
;(def top-tags-samples (sql {:name          sql-top-tags-samples
;                            :result-set-fn top-tags-samples-transformer
;                            :row-fn        parse-sample}))
;(defn get-top-tags-samples [db {:keys [offset count]}]
;  (let [samples (top-tags-samples db)]
;    (take count (drop offset samples))))


;;======================================================================================================================
;; Sitemap
;;======================================================================================================================
(def sitemap-sample-urls (sql {:name   sql-sitemap-sample-urls
                               :row-fn underscore->dash}))