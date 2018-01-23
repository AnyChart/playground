(ns playground.data.tags-macros
  (:require [cheshire.core :as json]
            [clj-http.client :as http]))


(defn get-data []
  (try
    (json/parse-string (:body (http/get "https://static.anychart.com/utility/tags_list.json?v3")) true)
    (catch Exception _ nil)))


(defmacro parse-data-compile-time []
  `'~(get-data))
