(ns playground.generator.data-sets
  (:require [cheshire.core :as json]
            [clojure.string :as string]
            [taoensso.timbre :as timbre]
            [playground.db.request :as db-req])
  (:import (jdk.nashorn.internal.runtime.options Options)
           (jdk.nashorn.internal.runtime ErrorManager Source Context)
           (jdk.nashorn.internal.parser Parser)
           (jdk.nashorn.api.scripting ScriptUtils)
           (javax.script ScriptEngineManager)))

(defn eval-some-javascript [s]
  (-> (ScriptEngineManager.)
      (.getEngineByMimeType "application/javascript")
      (.eval s)))

(defn parse-js-data-set [s]
  (eval-some-javascript (str "window = {};" s "JSON.stringify(window.anydata.datasets.pop());")))

(defn relative-path [full-url url]
  (let [parts (clojure.string/split full-url #"/")]
    (str (string/join "/" (drop-last parts)) (subs url 1))))

(defn full-url [full-url url]
  (cond
    (nil? url) nil
    (.startsWith url "http") url
    (.startsWith url "//") (str "http://" url)
    (.startsWith url "./") (relative-path full-url url)))

(defn parse-data-set [db data-source data-set]
  (let [data-set-url (:data data-set)
        full-data-set-url (full-url (:url data-source) data-set-url)
        raw-data (-> full-data-set-url slurp string/trim-newline string/trim)
        data (json/parse-string (cond
                                  (.endsWith data-set-url ".js") (parse-js-data-set raw-data)
                                  (.endsWith data-set-url ".json") raw-data) true)
        data-set (merge data-set data)
        db-data {:data-source-id (:id data-source)
                 :name           (:id data-set)
                 :title          (:name data-set)
                 :tags           (:tags data-set)
                 :logo           (full-url full-data-set-url (:logo data-set))
                 :description    (:description data-set)
                 :source         (:source data-set)
                 :sample         (:sample data-set)
                 :url            full-data-set-url}]
    (db-req/add-data-set<! db (update db-data :tags json/generate-string))))

(defn parse-data-source [db data-sources]
  (timbre/info data-sources db)
  (db-req/delete-data-sets! db)
  (db-req/delete-data-sources! db)
  (doseq [[name url] data-sources]
    (let [raw-data (slurp url)
          data (json/parse-string raw-data true)
          db-data {:name  (:id data)
                   :title (:name data)
                   :type  (:type data)
                   :sets  (:sets data)
                   :url   url}
          data-source-id (db-req/add-data-source<! db (update db-data :sets json/generate-string))
          full-db-data (assoc db-data :id data-source-id)]
      (doseq [data-set (:sets data)]
        (parse-data-set db full-db-data data-set)))))