(ns playground.web.chartopedia
  (:require [cheshire.core :as json]
            [me.raynes.fs :as fs]))


(defn get-chart-type [chart-type]
  (merge chart-type
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/chart-types/" (:id chart-type) ".json"))
           true)
         {:img (str "/chartopedia/images/chart-type/" (:id chart-type) ".png")}))


(defn get-category [category]
  (merge category
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/categories/" (:id category) ".json"))
           true)
         {:img (str "/chartopedia/images/categories/" (:id category) ".png")}))


(defn parse-data []
  (let [base (json/parse-string (slurp "resources/chartopedia/data/main.json") true)
        chart-types (map #(get-chart-type %) (:chartTypes base))
        relations (:relations base)
        categories (map #(get-category %) (:categories base))]
    {:chart-types chart-types
     :relations   relations
     :categories  categories}))


(defn copy-data []
  (fs/delete-dir "resources/public/chartopedia")
  (fs/copy-dir-into "resources/chartopedia/images" "resources/public/chartopedia/images"))


(defn prepare-data []
  (copy-data)
  (parse-data))

(defmacro parse-data-compile-time []
  `'~(prepare-data))


(def data (parse-data-compile-time))
(def chart-types (:chart-types data))
(def relations (:relations data))
(def categories (:categories data))


(defn get-chart [name]
  (first (filter #(= (:id %) name) chart-types)))


(defn get-relations [chart]
  (let [result-relations (reduce (fn [res relation]
                                   (if (some #(= (:name chart) %) (:charts relation))
                                     (let [new-res (reduce (fn [res chart-name]
                                                             (if (not= chart-name (:name chart))
                                                               (update res chart-name conj (:name relation))
                                                               res))
                                                           res
                                                           (:charts relation))]
                                       new-res)
                                     res))
                                 {}
                                 relations)
        result-relations* (map (fn [[name relations]] {:name      name
                                                       :relations (sort relations)}) result-relations)]
    (sort-by :name result-relations*)))