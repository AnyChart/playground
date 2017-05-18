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

(defn parse-js-to-ast [s]
  (let [options (doto (Options. "nashorn")
                  (.set "anon.functions" true)
                  (.set "parse.only" true)
                  (.set "scripting" true))
        errors (ErrorManager.)
        context (Context. options errors (.getContextClassLoader (Thread/currentThread)))]
    (Context/setGlobal (.createGlobal context))
    (let [json-str (ScriptUtils/parse s "test" false)]
      (json/parse-string json-str true))))

(defn eval-some-javascript [s]
  (-> (ScriptEngineManager.)
      (.getEngineByMimeType "application/javascript")
      (.eval s)))

(defn parse-js-obj [s]
  (json/parse-string (eval-some-javascript (str "JSON.stringify(" s ");")) true))

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

(defn parse-data-set [db data-source data-set-url]
  (let [full-data-set-url (full-url (:url data-source) data-set-url)
        raw-data (-> full-data-set-url slurp string/trim-newline string/trim)
        data (json/parse-string (parse-js-data-set raw-data) true)
        db-data {:data-source-id (:id data-source)
                 :name           (:id data)
                 :title          (:name data)
                 :tags           (:tags data)
                 :logo           (full-url full-data-set-url (:logo data))
                 :description    (:description data)
                 :source         (:source data)
                 :sample         (:sample data)
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
      (doseq [data-set-url (:sets data)]
        (parse-data-set db full-db-data data-set-url)))))