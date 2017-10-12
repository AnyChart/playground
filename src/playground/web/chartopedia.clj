(ns playground.web.chartopedia
  (:require [cheshire.core :as json]
            [me.raynes.fs :as fs]))


(defn parse-chart-type [chart-type]
  (merge chart-type
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/chart-types/" (:id chart-type) ".json"))
           true)
         {:img (str "/chartopedia/images/chart-type/" (:id chart-type) ".png")}))


(defn parse-category [category]
  (merge category
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/categories/" (:id category) ".json"))
           true)
         {:img (str "/chartopedia/images/categories/" (:id category) ".svg")}))

(defn get-chart-by-name [name charts]
  (first (filter #(= (:name %) name) charts)))

(defn get-chart-by-id [id charts]
  (first (filter #(= (:id %) id) charts)))


(defn parse-data []
  (let [base (json/parse-string (slurp "resources/chartopedia/data/main.json") true)
        chart-types (map #(parse-chart-type %) (:chartTypes base))
        relations (:relations base)
        categories (map #(parse-category %) (:categories base))
        categories (map (fn [category]
                          (update category :charts
                                  (fn [charts]
                                    (map (fn [chart-id]
                                           (get-chart-by-id chart-id chart-types))
                                         charts))))
                        categories)]
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


(defn get-category [name]
  (first (filter #(= (:id %) name) categories)))


(defn get-relations [chart]
  (let [result-relations (reduce (fn [res relation]
                                   (if (some #(= (:id chart) %) (:charts relation))
                                     (reduce (fn [res chart-id]
                                               (if (not= chart-id (:id chart))
                                                 (update res chart-id conj (:name relation))
                                                 res))
                                             res
                                             (:charts relation))
                                     res))
                                 {}
                                 relations)
        result-relations* (map (fn [[id relations]] {:name      (:name (get-chart-by-id id chart-types))
                                                     :id        id
                                                     :relations (sort relations)}) result-relations)]
    (sort-by :name result-relations*)))