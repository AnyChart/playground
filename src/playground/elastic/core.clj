(ns playground.elastic.core
  (:require [playground.elastic.helpers :refer [bulk-samples prepare-sample]]
            [playground.elastic.consts :as elastic-consts]
            [playground.elastic.init :as elastic-init]
            [qbits.spandex :as s]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [clojure.core.async :as async]
            [clojure.java.jdbc :as jdbc]
            [instaparse.core :as insta]
            [clojure.string :as string]
            [com.stuartsierra.component :as component]
            [playground.db.request :as db-req]
            [playground.utils.utils :as utils]))


;; =====================================================================================================================
;; Component
;; =====================================================================================================================
(defrecord Elastic [conf db conn]
  component/Lifecycle
  (start [this]
    (timbre/info "ElasticSearch component start")
    (let [comp (assoc this :conn (s/client {:hosts [(:host conf)]}))]
      ;(elastic-init/init (assoc comp :db db :conf conf))
      comp))
  (stop [this]
    (timbre/info "ElasticSearch component stop")
    (s/close! (:conn this))))


(defn new-elastic [conf]
  (map->Elastic {:conf conf}))


(defn get-connection [elastic]
  (:conn elastic))


;; =====================================================================================================================
;; Samples requests
;; =====================================================================================================================
(defn sample-by-id [id conf]
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:query
                                         {:bool
                                          {:filter
                                           [{:term {:id id}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic sample-by-id error:" e))))


;(defn remove-sample-by-id [id conf]
;  (try
;    (let [conn (get-connection conf)
;          data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
;                                :method :post
;                                :body   {:query {:bool {:filter [{:term {:id id}}]}}}})]
;      data)
;    (catch Exception e (timbre/error "Elastic remove-sample-by-id error:" (pr-str e)))))


(defn sample-by-url [url conf]
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:query
                                         {:bool
                                          {:filter
                                           [{:term {:url url}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic sample-by-url error:" (pr-str e)))))


(defn remove-sample-by-url [{conn :conn conf :conf} url]
  (timbre/info "Elastic remove sample by url:" url)
  (try
    (let [data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
                                :method :post
                                :body   {:query {:bool {:filter [{:term {:url url}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic remove-sample-by-url error:" (pr-str e)))))


(defn add-sample [{conn :conn conf :conf} sample]
  (timbre/info "Elastic: add sample" (:url sample))
  (try
    (s/request conn {:url    [(:index conf) (:type conf)]
                     :method :post
                     :body   (prepare-sample sample)})
    (catch Exception e (timbre/error "Elastic add-sample error:" (pr-str e)))))


(defn replace-sample [elastic sample]
  (remove-sample-by-url elastic (:url sample))
  (add-sample elastic sample))


;; =====================================================================================================================
;; Remove branch
;; =====================================================================================================================
(defn remove-branch [{conn :conn conf :conf} repo-name version-name]
  (timbre/info "Remove branch:" repo-name version-name)
  (try
    (let [data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
                                :method :post
                                :body   {:query {:bool {:filter [{:term {:repo-name repo-name}}
                                                                 {:term {:version-name version-name}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic remove branch error:" (pr-str e)))))


(defn add-branch [{conn :conn conf :conf} samples repo-name version-name version-id]
  (timbre/info "Add branch:" repo-name version-name)
  (let [samples (map #(prepare-sample % repo-name version-name version-id) samples)
        samples-list (bulk-samples samples conf)]
    (try
      (let [data (s/request conn {:url    "/_bulk"
                                  :method :put
                                  :body   (s/chunks->body samples-list)})]
        data
        nil)
      (catch Exception e (timbre/error "Elastic add branch error:" (pr-str e))))))


(defn update-branch [db elastic repo-name version-name version-id]
  (remove-branch elastic repo-name version-name)
  (let [samples (db-req/elastic-samples-version db {:version-id version-id})]
    (add-branch elastic samples repo-name version-name version-id)))


;; =====================================================================================================================
;; Search
;; =====================================================================================================================
; Parser for search string:
; project:api version:8.1.0 tag:'line chart' chart name
(def parser
  (insta/parser
    "
    <main> = wo*

    <wo> = ws? (word | option) ws?

    word =  #'[^\\s]+'

    <option> = (project | version | tag)

    project = project_key ws? <':'> ws? val
    <project_key> = <'project'> | <'proj'> | <'p'>

    version = version_key ws? <':'> ws? val
    <version_key> = <'version'> | <'ver'> | <'v'>

    tag = tag_key ws? <':'> ws? val
    <tag_key> = <'tag'> | <'t'>

    <val> = val_ | (<'\\''> #'[^\\']*' <'\\''>) | (<'\"'> #'[^\"]*' <'\"'>)
    <val_> = #'[a-zA-Z0-9-\\._]+'

    <ws> = <#'\\s+'>"))


(defn parse [q]
  (let [parts (parser q)
        projects (filter #(= (first %) :project) parts)
        versions (filter #(= (first %) :version) parts)
        tags (filter #(= (first %) :tag) parts)
        words (filter #(= (first %) :word) parts)]
    {:projects (map second projects)
     :versions (map second versions)
     :tags     (map second tags)
     :q        (string/join " " (map second words))}))


(defn e-query [{:keys [projects versions tags q]}]
  (let [projects-filter (case (count projects)
                          0 nil
                          1 {:term {:repo-name (first projects)}}
                          {:bool {:should (mapv (fn [p] {:term {:repo-name p}}) projects)}})

        versions-filter (case (count versions)
                          0 {:term {:latest true}}
                          1 {:term {:version-name (first versions)}}
                          {:bool {:should (mapv (fn [v] {:term {:version-name v}}) versions)}})

        tags-filter (map (fn [t] {:match {:tags-kw t}}) tags)
        must-filter (filter some? (concat [projects-filter versions-filter] tags-filter))]

    {:bool {:filter {:bool {:must must-filter}}
            :must   (when (seq q) {:bool {:should [{:match {:name q}}
                                                   {:match {:tags-kw q}}
                                                   {:match {:tags q}}
                                                   {:match {:description q}}
                                                   {:match {:short-description q}}]}})}}))


;; =====================================================================================================================
;; Pagination Requests Utils
;; =====================================================================================================================
(defn get-max-page [total items-per-page]
  (dec (int (Math/ceil (/ total items-per-page)))))


(defn make-result [samples total size offset]
  (let [total (min total elastic-consts/elastic-max-result-window)]
    {:samples  (map #(assoc % :full-url (utils/sample-url %)) samples)
     :total    total
     :end      (<= (- total offset) size)
     :max-page (get-max-page total size)}))


(defn search [{conn :conn conf :conf} q offset size]
  (try
    (let [data (parse q)
          ;_ (prn "Data: " data)
          query (e-query data)
          ;_ (clojure.pprint/pprint query)
          data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:size    size
                                         :from    offset
                                         :_source {:excludes [:name-kw :tags-kw]}
                                         :query   query}})
          hits (:hits (:body data))
          total (:total hits)
          samples (map (fn [hit]
                         (assoc (:_source hit) :score (:_score hit)))
                       (:hits hits))]
      (timbre/info "Search:" q ", total:" total ", max Score:" (:max_score hits))
      (make-result samples total size offset))
    (catch Exception e (timbre/error "Search error:" (pr-str e)))))


;; =====================================================================================================================
;; Top samples
;; =====================================================================================================================
(defn top-samples [{conn :conn conf :conf} offset size]
  (try
    (let [data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:size    size
                                         :from    offset
                                         :sort    [{"likes" {:order "desc"}}
                                                   {"views" {:order "desc"}}
                                                   {"name-kw" {:order "asc"}}]
                                         :_source {:excludes [:name-kw :tags-kw]}
                                         :query   {:term {:latest true}}}})
          hits (:hits (:body data))
          total (:total hits)
          samples (map (fn [hit]
                         (assoc (:_source hit) :score (:_score hit)))
                       (:hits hits))]
      (make-result samples total size offset))
    (catch Exception e (timbre/error "Elastic top samples error:" (pr-str e)))))


;; =====================================================================================================================
;; Version samples
;; =====================================================================================================================
(defn version-samples [{conn :conn conf :conf} version-id offset size]
  (try
    (let [data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:size    size
                                         :from    offset
                                         :sort    [{"likes" {:order "desc"}}
                                                   {"views" {:order "desc"}}
                                                   {"name-kw" {:order "asc"}}]
                                         :_source {:excludes [:name-kw :tags-kw]}
                                         :query   {:term {:version-id version-id}}}})
          hits (:hits (:body data))
          total (:total hits)
          samples (map :_source (:hits hits))]
      (make-result samples total size offset))
    (catch Exception e (timbre/error "Elastic top samples error:" (pr-str e)))))


;; =====================================================================================================================
;; Tag samples
;; =====================================================================================================================
(defn tag-samples [{conn :conn conf :conf} tag offset size]
  (try
    (let [data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:size    size
                                         :from    offset
                                         :sort    [{"likes" {:order "desc"}}
                                                   {"views" {:order "desc"}}
                                                   {"name-kw" {:order "asc"}}]
                                         :_source {:excludes [:name-kw :tags-kw]}
                                         :query   {:bool {:filter [{:match {:tags-kw tag}}
                                                                   {:match {:latest true}}]}}}})
          hits (:hits (:body data))
          total (:total hits)
          samples (map :_source (:hits hits))]
      (make-result samples total size offset))
    (catch Exception e (timbre/error "Elastic tag samples error:" (pr-str e)))))