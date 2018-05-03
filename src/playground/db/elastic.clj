(ns playground.db.elastic
  (:require [qbits.spandex :as s]
            [cheshire.core :as json]
            [playground.db.request :as db-req]
            [taoensso.timbre :as timbre]
            [clojure.core.async :as async]
            [clojure.java.jdbc :as jdbc]
            [instaparse.core :as insta]
            [clojure.string :as string]))

; :headers {"Content-Type" "application/json; charset=UTF-8"}

;; =====================================================================================================================
;; Constants
;; =====================================================================================================================
(def mapping-default {:properties
                      {:description       {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :create-date       {:type "date"},
                       :tags              {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :short-description {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :name              {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :version-id        {:type "long"},
                       :likes             {:type "long"},
                       :repo-name         {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :full-url          {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :latest            {:type "boolean"},
                       :preview           {:type "boolean"},
                       :id                {:type "long"},
                       :version-name      {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :url               {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :version           {:type "long"},
                       :views             {:type "long"}}})


(def mapping {:properties
              {:description       {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :create-date       {:type "date"}
               :tags              {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :short-description {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :name              {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :version-id        {:type "long"}
               :likes             {:type "long"}
               :repo-name         {:type "keyword"}
               :full-url          {:type "keyword"}
               :latest            {:type "boolean"}
               :preview           {:type "boolean"}
               :id                {:type "long"}
               :version-name      {:type "keyword"}
               :url               {:type "keyword"}
               :version           {:type "long"}
               :views             {:type "long"}}})



(def conf {:host  "http://127.0.0.1:9200"
           :index "pg_local"
           :type  "samples"})


(def sample {:description       ""
             :create-date       "2018-02-22T08:55:35Z"
             :tags              ["Tag Cloud" "Weighted List Chart" "Word Cloud"]
             :short-description ""
             :name              "BCT Tag Cloud Chart 13"
             :version-id        246
             :likes             0
             :repo-name         "docs"
             :full-url          "/docs/8.1.0/samples/BCT_Tag_Cloud_Chart_13"
             :latest            true
             :preview           true
             :id                133737
             :version-name      "8.1.0"
             :url               "samples/BCT_Tag_Cloud_Chart_13"
             :version           0
             :views             0})

;; =====================================================================================================================
;; Helpers
;; =====================================================================================================================
(defn get-connection [conf]
  (s/client {:hosts [(:host conf)]}))


(defn prepare-sample
  ([sample] (prepare-sample sample nil nil))
  ([sample repo-name version-name]
   (-> sample
       (select-keys (keys (:properties mapping)))
       (update :repo-name #(or % repo-name))
       (update :version-name #(or % version-name)))))


(defn bulk-samples [samples conf]
  (mapcat (fn [sample]
            [{:index {:_index (:index conf)
                      :_type  (:type conf)}}
             sample])
          samples))


;; =====================================================================================================================
;; Init elastic
;; =====================================================================================================================
(defn delete-index [conf]
  (try
    (timbre/info "Elastic delete index")
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf)]
                                :method :delete})]
      (timbre/info "Elastic delete index:" data))
    (catch Exception e (timbre/error "Delete index error, probably already deleted"))))


(defn create-index [conf]
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf)]
                                :method :put})]
      data)
    (catch Exception e (timbre/error "set mapping error:" (pr-str e)))))


(defn set-mapping [conf]
  (timbre/info "Elastic set mapping")
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) :_mapping (:type conf)]
                                :method :post
                                :body   mapping})]
      data)
    (catch Exception e
      (timbre/error "Elastic set mapping error:" (pr-str e)))))


(defn load-samples [db conf]
  (timbre/info "Elastic load samples")
  (try
    (let [conn (get-connection conf)
          samples (db-req/search-samples db)
          samples-groups (partition-all 20000 samples)]
      (timbre/info "Elastic load samples total:" (count samples))
      (doseq [samples samples-groups]
        (let [samples-list (bulk-samples samples conf)]
          (timbre/info "Elastic load samples: " (count samples))
          (s/request conn {:url    "/_bulk"
                           :method :put
                           :body   (s/chunks->body samples-list)}))))
    (catch Exception e (timbre/error "Elastic load samples error:" (pr-str e)))))


(defn init [db conf]
  (timbre/info "Elastic init")
  (jdbc/with-db-transaction [conn (:db-spec db) {:isolation :serializable}]
                            (delete-index conf)
                            (create-index conf)
                            (set-mapping conf)
                            (load-samples conn conf)))


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


(defn remove-sample-by-id [id conf]
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
                                :method :post
                                :body   {:query {:bool {:filter [{:term {:id id}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic remove-sample-by-id error:" (pr-str e)))))


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


(defn remove-sample-by-url [url conf]
  (timbre/info "Elastic remove sample by url:" url)
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
                                :method :post
                                :body   {:query {:bool {:filter [{:term {:url url}}]}}}})]
      data)
    (catch Exception e (timbre/error "Elastic remove-sample-by-url error:" (pr-str e)))))


(defn add-sample [sample conf]
  (timbre/info "Elastic: add sample" (:url sample))
  (try
    (let [conn (get-connection conf)]
      (s/request conn {:url    [(:index conf) (:type conf)]
                       :method :post
                       :body   (prepare-sample sample)}))
    (catch Exception e (timbre/error "Elastic add-sample error:" (pr-str e)))))


(defn replace-sample [sample conf]
  (remove-sample-by-url (:url sample) conf)
  (add-sample sample conf))


;; =====================================================================================================================
;; Remove branch
;; =====================================================================================================================
(defn remove-branch [repo-name version-name conf]
  (timbre/info "Remove branch:" repo-name version-name)
  (let [conn (get-connection conf)]
    (try
      (let [data (s/request conn {:url    [(:index conf) (:type conf) :_delete_by_query]
                                  :method :post
                                  :body   {:query {:bool {:filter [{:term {:repo-name repo-name}}
                                                                   {:term {:version-name version-name}}]}}}})]
        data)
      (catch Exception e (timbre/error "Elastic remove branch error:" (pr-str e))))))


(defn add-branch [samples repo-name version-name conf]
  (timbre/info "Add branch:" repo-name version-name)
  (let [conn (get-connection conf)
        samples (map #(prepare-sample % repo-name version-name) samples)
        samples-list (bulk-samples samples conf)]
    (try
      (let [data (s/request conn {:url    "/_bulk"
                                  :method :put
                                  :body   (s/chunks->body samples-list)})]
        data
        nil)
      (catch Exception e (timbre/error "Elastic add branch error:" (pr-str e))))))


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

        tags-filter (map (fn [t] {:match {:tags t}}) tags)
        must-filter (filter some? (concat [projects-filter versions-filter] tags-filter))]

    {:bool {:filter {:bool {:must must-filter}}
            :must   (when (seq q) {:bool {:should [{:match {:name q}}
                                                   {:match {:tags q}}
                                                   {:match {:description q}}
                                                   {:match {:short-description q}}]}})}}))


(defn search [conf q offset size]
  (try
    (let [data (parse q)
          ;_ (prn "Data: " data)
          query (e-query data)
          ;_ (clojure.pprint/pprint query)
          conn (get-connection conf)
          data (s/request conn {:url    [(:index conf) (:type conf) :_search]
                                :method :post
                                :body   {:size  size
                                         :from  offset
                                         :query query}})
          hits (:hits (:body data))
          total (:total hits)
          samples (map (fn [hit]
                         (assoc (:_source hit) :score (:_score hit)))
                       (:hits hits))
          ;samples (map #(select-keys % [:name :tags]) samples)
          ]
      (timbre/info "Search - Total:" total ", Max Score:" (:max_score hits))
      ;(println :elastic (:body data))
      {:samples samples
       :total   total})
    (catch Exception e (timbre/error "Search error:" (pr-str e)))))



(defn get-all [conf]
  (try
    (let [conn (get-connection conf)
          data (s/request conn {:url    [:pg_local :samples :_search]
                                :method :get
                                :body   {:query {:match_all {}}}})
          data (:hits (:body data))
          total (:total data)
          hits (map :_source (take 10 (:hits data)))]
      (prn total (keys data)))
    (catch Exception e (timbre/error (pr-str e)))))
