(ns playground.data.tags
  (:require [clojure.string :as string]
            [clojure.set :as set])
  #?(:clj  (:require [playground.data.tags-macros :as tags-macros])
     :cljs (:require-macros [playground.data.tags-macros :as tags-macros])))

(defn original-name->id-name [name]
  (-> name
      string/lower-case
      (string/replace #"[^a-z0-9]" "-")
      (string/replace #"-[-]+" "-")
      (string/replace #"-$" "")
      (string/replace #"^-" "")))


(def tags-data (tags-macros/parse-data-compile-time))

(def rules (:rules tags-data))


(defn get-all-tags []
  (let [
        tags-from-rules (distinct (mapcat :tags rules))
        ;tags (map name (keys (:tags tags-data)))
        ;all-tags (sort (distinct (concat tags-from-rules tags)))
        all-tags (map (fn [tag-name]
                        (assoc {}
                          :name tag-name
                          :id (original-name->id-name tag-name)
                          :description (or (:description ((keyword tag-name) (:tags tags-data))) "")
                          ))
                      tags-from-rules)]
    (sort-by :name all-tags)))

(def all-tags (get-all-tags))

;; for tags page
(defn get-tag-data [tag]
  (first (filter (fn [tag-data]
                   (or (= tag (:id tag-data))
                       (= tag (:name tag-data)))) all-tags)))

;; for generation
(defn get-tags-by-code [code]
  (let [tags-code (reduce (fn [res {:keys [regexp tags]}]
                            (if (string/includes? code regexp)
                              (concat res tags)
                              res)) [] rules)]
    (sort (distinct tags-code))))

;; whether anychart tag or user tag
(defn anychart-tag? [tag]
  (some (fn [tag-data]
          (or (= tag (:id tag-data))
              (= tag (:name tag-data))))
        all-tags))

(defn original-name-by-id [tag]
  (:name (first (filter (fn [tag-data]
                          (= tag (:id tag-data)))
                        all-tags))))


(defn check-data []
  (let [tags-from-rules (mapcat :tags (:rules tags-data))
        tags (map name (keys (:tags tags-data)))
        bad-tags (set/difference (set tags-from-rules) (set tags))]
    (prn (set tags-from-rules))
    (prn (set tags))
    bad-tags))