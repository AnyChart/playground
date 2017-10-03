(ns playground.web.tags-macros
  (:require [cheshire.core :as json]))

(defn prepare-data []
  (json/parse-string (slurp "https://static.anychart.com/utility/tags_list.json") true))

(defmacro parse-data-compile-time []
  `'~(prepare-data))
