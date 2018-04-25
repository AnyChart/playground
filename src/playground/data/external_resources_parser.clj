(ns playground.data.external-resources-parser
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.java.io :as io]
            [instaparse.core :as insta]
            [clojure.string :as string]
            [me.raynes.fs :as fs])
  (:import (java.io File)))


(defn get-data []
  (try
    (json/parse-string (:body (http/get "https://cdn.anychart.com/releases/v8/js/modules.json")) true)
    (catch Exception _ nil)))


(defmacro parse-data-compile-time []
  `'~(get-data))


;;======================================================================================================================
;; modules.json generator for v7
;;======================================================================================================================

(defn get-locales []
  (let [path "/media/ssd/sibental/playground-data/MODULES V7 GENERATION/locales"
        files (file-seq (io/file path))
        files (filter
                #(not (.isDirectory %)) files)
        data (doall (map (fn [file]
                           (let [data (slurp file)
                                 lines (string/split-lines data)
                                 get-data (fn [line]
                                            (let [parts (string/split line #"\s*:")
                                                  k (string/trim (first parts))
                                                  v (string/trim (second parts))
                                                  v (subs v (inc (string/index-of v "'")) (string/last-index-of v "'"))
                                                  v (string/trim v)]
                                              {(keyword k) v}))
                                 info (merge
                                        (get-data (nth lines 1))
                                        (get-data (nth lines 2))
                                        (get-data (nth lines 3)))
                                 info {:code        (:code info)
                                       :eng-name    (:engName info)
                                       :native-name (:nativeName info)
                                       :file-name   (.getName file)}]
                             ;(.renameTo file (io/file (str path File/separator (:code info) ".js")))
                             info))
                         files))
        data (sort-by :code data)
        ;d (group-by :code data)
        ;d (filter (fn [[k val ]]
        ;            (> (count val) 1)
        ;            ) d )
        locales (map (fn [item] {(:code item) (select-keys item [:eng-name :native-name])}) data)
        ;locales {:locales locales}
        ]
    locales
    ))


(defn all []
  (let [all (get-data)
        data {:themes  (:themes all)
              :geodata (:geodata all)
              :modules [{:anychart-bundle {:type "bundle"
                                           :name "AnyChart Bundle"
                                           :desc "AnyChart Bundle module"}}
                        {:anychart-ui {:size 21
                                       :name "Common UI"
                                       :docs ""
                                       :icon "/_design/img/upload/charts/types/area-chart.svg"
                                       :type "feature"
                                       :desc "Context menu, range picker, range selector and preloader module."}}]

              :locales (get-locales)}
        s (json/generate-string data)]
    (spit (io/file "/media/ssd/sibental/playground-data/modules-v7.json") s)))


;;======================================================================================================================
;; modules.json generator for v8 8.0.0 8.0.1
;;======================================================================================================================
(defn go []
  (let [data1 (json/parse-string (slurp "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.1.0.json") true)
        data2 (json/parse-string (slurp "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.0.0.json") true)
        new-data (assoc data2
                   :locales (:locales data1)
                   :geodata (:geodata data1))]
    (prn (keys data1))
    (prn (keys data2))
    (prn (keys new-data))
    (spit (io/file "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.0.0-new.json") (json/generate-string new-data))))