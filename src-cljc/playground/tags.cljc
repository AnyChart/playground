(ns playground.tags
  #?(:clj
     (:require [playground.web.tags-macros :as macros])
     :cljs
     (:require-macros [playground.web.tags-macros :as macros])))

(def tags-data (macros/parse-data-compile-time))

(defn get-tag-data [tag]
  (second
    (first
      (filter
        (fn [[tag-name data]]
          (= (name tag-name) tag))
        (:tags tags-data)))))