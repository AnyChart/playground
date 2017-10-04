(ns playground.tags
  #?(:clj
     (:require [playground.web.tags-macros :as macros])
     :cljs
     (:require-macros [playground.web.tags-macros :as macros])))

(def tags-data (macros/parse-data-compile-time))

(defn get-all-tags []
  (let [rules (:rules tags-data)
        tags-from-rules (mapcat :tags rules)
        tags (map name (keys (:tags tags-data)))
        all-tags (sort (distinct (concat tags-from-rules tags)))]
    all-tags))

(def all-tags (get-all-tags))

;; for tags page
(defn get-tag-data [tag]
  (second
    (first
      (filter
        (fn [[tag-name data]]
          (= (name tag-name) tag))
        (:tags tags-data)))))

;; for generation
(defn get-tags-by-code [code]
  (let [rules (:rules tags-data)
        tags-code (reduce (fn [res {:keys [regexp tags]}]
                            (if (.contains code regexp)
                              (concat res tags)
                              res)) [] rules)]
    (sort (distinct tags-code))))