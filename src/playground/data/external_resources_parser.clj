(ns playground.data.external-resources-parser
  (:require [cheshire.core :as json]
            [clj-http.client :as http]))


(defn get-data []
  (try
    (json/parse-string (:body (http/get "https://cdn.anychart.com/releases/latest-v8/js/modules.json")) true)
    (catch Exception _ nil)))


(defmacro parse-data-compile-time []
  `'~(get-data))
