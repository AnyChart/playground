(ns playground.web.sitemap
  (:require [clojure.xml :as xml]
            [clojure.string :as string]
            [playground.web.chartopedia :as chartopedia]
            [clj-time.format :as f]
            [clj-time.coerce :as c])
  (:import (java.time LocalDateTime)))

(defn now []
  (.toString (LocalDateTime/now)))

(defn full-url [& parts]
  (str "https://pg.anychart.com"
       (string/join ""
                    (map #(string/replace % #" " "%20") parts))))

(def default-date "2017-10-30T10:43:55Z")

(defn date->str [date-time]
  (-> date-time
      (string/replace #" " "T")
      (subs 0 19)
      (str "Z")))

(def static-urls ["/"
                  "/chart-types"
                  "/chart-types/categories"

                  "/tags"

                  "/support"
                  "/roadmap"
                  "/version-history"

                  "/about"])

(defn static-tag [url]
  {:tag     :url
   :content [{:tag :loc :content [(full-url url)]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn tag-tag [tag]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/tags/" (:name tag))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn chart-type [chart-type]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/chart-types/" (:id chart-type))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn chart-type-category [category]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/chart-types/categories/" (:id category))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn data-set [data-set]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/datasets/" (:name data-set))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn repo [repo]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/projects/" (:name repo))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})

(defn version [version]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/projects/" (:repo-name version) "/" (:name version))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [default-date]}]})


(defn sample [sample]
  {:tag     :url
   :content [{:tag :loc :content [(full-url "/" (:url sample))]}
             {:tag :priority :content ["0.5"]}
             {:tag :changefreq :content ["monthly"]}
             {:tag :lastmod :content [(date->str (:create-date sample))]}]})


(defn page [data]
  (with-out-str
    (xml/emit {:tag     :urlset
               :attrs   {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
               :content (concat
                          (map static-tag static-urls)
                          (map repo (filter #(not (:templates %)) (:repos data)))
                          (map version (:versions data))

                          (map tag-tag (:all-tags data))
                          (map chart-type chartopedia/chart-types)
                          (map chart-type-category chartopedia/categories)

                          ;(map data-set (:all-data-sets data))
                          (map sample (:samples data)))})))