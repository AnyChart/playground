(ns playground.generator.parser.sample-parser
  (:require [net.cgrand.enlive-html :as html]
            [taoensso.timbre :refer [info error]]
            [clojure.string :as s :refer (trim-newline)]
            [clojure.java.io :refer [file]]
            [toml.core :as toml]
            [playground.data.tags :as tags-data]
            [clojure.string :as string])
  (:import (org.jsoup Jsoup)))

(defn- ^String trim-newline-left [^CharSequence s]
  (loop [index 0]
    (if (= 0 (.length s))
      ""
      (let [ch (.charAt s index)]
        (if (or (= ch \newline) (= ch \return))
          (recur (inc index))
          (.. s (subSequence index (.length s)) toString))))))

(defn trim-trailing [s]
  (clojure.string/replace s #"\s*$" ""))

(defn space-count [s]
  (loop [index 0]
    (if (= (.charAt s index) \space)
      (recur (inc index))
      index)))

(defn trim-code
  "Delete so many spaces from string start as it have at first line"
  [s]
  (when (and s (seq s))
    (let [trailing-s (-> s trim-trailing trim-newline-left)
          space-count (space-count (last (string/split-lines trailing-s)))
          pattern (re-pattern (str "(?m)^[ ]{" space-count "}"))]
      (clojure.string/replace trailing-s pattern ""))))


(defn parse-html-sample [s]
  (let [page (Jsoup/parse s)

        scripts (some->> (.select page "script[src]")
                         (map (fn [script] (.attr script "src"))))

        code (.select page "script:not([src])")
        code (if code (.html code) "")

        markup (.select page "body > :not(script)")
        markup (string/join "\n" markup)

        style (.select page "style")
        style (string/join "\n" (map #(.html %) style))

        styles (.select page "link[rel=stylesheet][href]")
        styles (map (fn [style] (.attr style "href")) styles)

        name (.select page "meta[name=ac:name]")
        name (.attr name "content")

        exports (.select page "meta[name=ac:export]")
        exports (.attr exports "content")

        description (.select page "meta[name=ac:desc]")
        description (.attr description "content")

        short-description (.select page "meta[name=ac:short-desc]")
        short-description (.attr short-description "content")

        tags-content (.select page "meta[name=ac:tags]")
        tags-content (.attr tags-content "content")

        tags (if (and tags-content
                      (seq tags-content))
               (string/split tags-content #"\s*,\s*")
               [])
        all-tags (sort (distinct (concat tags (tags-data/get-tags-by-code code))))]
    {:name              name
     :description       (string/trim description)
     :short-description (string/trim short-description)

     :tags              all-tags
     :deleted-tags      []
     :exports           exports

     :scripts           scripts

     :styles            styles

     :code-type         "js"
     :code              (or (trim-code code) "")

     :markup-type       "html"
     :markup            (or (trim-code markup) "")

     :style-type        "css"
     :style             (or (trim-code style) "")}))


(defn parse-toml-sample [path s]
  (try
    (let [data (toml/read s :keywordize)]
      {:name              (-> data :name)
       :description       (-> data :description)
       :short-description (-> data :short-description)

       :tags              (-> data :meta :tags)
       :exports           (-> data :meta :export)

       :scripts           (-> data :deps :scripts)
       :local-scripts     (-> data :deps :local-scripts)
       :styles            (-> data :deps :styles)

       :code-type         (-> data :code :type)
       :code              (-> data :code :code trim-code)

       :markup-type       (-> data :markup :type)
       :markup            (-> data :markup :code trim-code)

       :style-type        (-> data :style :type)
       :style             (-> data :style :code trim-code)})
    (catch Exception e
      (info "parse TOML error: " path e)
      nil)))


(defn- sample-path [base-path group sample]
  (if (.exists (file (str base-path group sample)))
    (str base-path group sample)
    (str base-path group "_samples/" sample)))


(defn replace-vars [s vars]
  (reduce (fn [s [key value]]
            (clojure.string/replace s
                                    (re-pattern (str "\\{\\{" (name key) "\\}\\}"))
                                    value))
          s vars))


(defn html? [s]
  (.startsWith (.toLowerCase s) "<!doctype html"))


(defn parse [base-path group config sample]
  ;(prn "parse sample: " base-path group config sample)
  (let [path (sample-path base-path group sample)
        name (clojure.string/replace sample #"\.(html|sample)$" "")
        sample-str (-> path slurp (replace-vars (:vars config)))
        base-info (cond (.endsWith path ".html") (parse-html-sample sample-str)
                        (.endsWith path ".sample") (if (html? sample-str)
                                                     (parse-html-sample sample-str)
                                                     (parse-toml-sample path sample-str)))]
    (when base-info
      (assoc base-info
        :name (or (:name base-info) (clojure.string/replace name #"_" " "))
        :hidden (= name "Coming_Soon")
        :url (str (.substring group 1)
                  (clojure.string/replace name #"%" "%25"))))))
