(ns playground.generator.parser.group-parser
  (:require [taoensso.timbre :refer [info error]]
            [clojure.java.io :refer [file]]
            [clojure.string :refer [re-quote-replacement]]
            [playground.generator.parser.sample-parser :as sample-parser]))

(defn to-folder-path [path]
  (if (.endsWith path "/") path (str path "/")))

(defn prettify-name [path]
  (clojure.string/replace path #"_" " "))

(defn fix-url [path]
  (clojure.string/replace path #" " "_"))

(defn get-group-config [config-path]
  (when (.exists (file config-path))
    (read-string (slurp config-path))))

(defn get-group-samples [group-path extensions]
  (let [files (concat (.listFiles (file group-path))
                      (.listFiles (file (str group-path "_samples"))))]
    (->> files
         (filter #(and (not (.isDirectory %))
                       (not (.isHidden %))
                       (some (fn [ext] (.endsWith (.getName %) (str "." ext))) extensions)))
         (map #(.getName %)))))

(defn get-groups-from-fs [path]
  (let [fpath (file path)]
    (->> fpath
         (tree-seq (fn [f] (and (.isDirectory f) (not (.isHidden f))))
                   (fn [d] (filter #(not (.isHidden %)) (.listFiles d))))
         (filter #(and (.isDirectory %)
                       (not (= (.getName %) "_samples"))
                       (not (= fpath %))))
         (map #(clojure.string/replace (.getAbsolutePath %)
                                       (re-quote-replacement (to-folder-path path)) "")))))

(defn- create-group-info [path group]
  ;(info "creating group:" group path (load-group-config path group))
  (let [group-path (str (to-folder-path path) (to-folder-path group))
        config-path (str group-path "group.cfg")
        samples (get-group-samples group-path #{"sample" "html" "toml"})]
    (merge {:index        1000
            :gallery-name (prettify-name group)
            :gallery-url  (fix-url group)}
           (get-group-config config-path)
           {:path    group
            :hidden  (or (= samples '("Coming_Soon.sample"))
                         (= group ""))
            :root    (= group "")
            :name    (prettify-name group)
            :samples (map #(sample-parser/parse (to-folder-path path) (to-folder-path group) %)
                          samples)})))

(defn groups [path]
  (info "Searching for samples in" path)
  (->> (get-groups-from-fs path)
       (map #(create-group-info path %))
       (filter #(seq (:samples %)))
       (cons (create-group-info path ""))
       (sort-by (juxt :index :name))))
