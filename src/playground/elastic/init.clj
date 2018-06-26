(ns playground.elastic.init
  (:require [playground.elastic.consts :as elastic-consts]
            [playground.elastic.helpers :refer [prepare-sample bulk-samples]]
            [taoensso.timbre :as timbre]
            [clojure.java.jdbc :as jdbc]
            [qbits.spandex :as s]
            [playground.db.request :as db-req]))


(defn get-connection [elastic]
  (:conn elastic))


(defn delete-index [{conn :conn conf :conf}]
  (try
    (timbre/info "Elastic delete index")
    (let [data (s/request conn {:url    [(:index conf)]
                                :method :delete})]
      (timbre/info "Elastic delete index:" data))
    (catch Exception e (timbre/error "Delete index error, probably already deleted"))))


(defn create-index [{conn :conn conf :conf}]
  (try
    (timbre/info "Elastic create index")
    (let [data (s/request conn {:url    [(:index conf)]
                                :method :put
                                :body   {:settings {:max_result_window elastic-consts/elastic-max-result-window
                                                    :analysis          {:normalizer
                                                                        {:lowercase_normalizer {"type"        "custom"
                                                                                                "char_filter" []
                                                                                                "filter"      ["lowercase"]}}}}
                                         :mappings {(:type conf) elastic-consts/mapping}}})]
      data)
    (catch Exception e (timbre/error "set mapping error:" (pr-str e)))))


(defn load-samples [conn elastic]
  (timbre/info "Elastic load samples")
  (try
    (let [conn (get-connection elastic)
          samples (db-req/search-samples (:db elastic))
          samples (map prepare-sample samples)
          samples-groups (partition-all elastic-consts/elastic-bulk-samples-count samples)]
      (timbre/info "Elastic load samples total:" (count samples))
      (doseq [samples samples-groups]
        (let [samples-list (bulk-samples samples (:conf elastic))]
          (timbre/info "Elastic load samples: " (count samples))
          (s/request conn {:url    "/_bulk"
                           :method :put
                           :body   (s/chunks->body samples-list)}))))
    (catch Exception e (timbre/error "Elastic load samples error:" (pr-str e)))))


(defn init [elastic]
  (timbre/info "Elastic init database")
  (jdbc/with-db-transaction [conn (:db-spec (-> elastic :db)) {:isolation :serializable}]
                            (delete-index elastic)
                            (create-index elastic)
                            (load-samples conn elastic)))
