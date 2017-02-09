(ns playground.generator.parser-old.fs
  (:require [playground.generator.parser-old.sample-parser :as sample-parser]
            [clojure.java.io :refer [file]]
            [clojure.string :refer [re-quote-replacement]]
            [taoensso.timbre :refer [info]]))

(defn to-folder-path [path]
  (if (.endsWith path "/")
    path
    (str path "/")))

(defn prettify-name [path]
  (clojure.string/replace path #"_" " "))

(defn fix-url [path]
  (clojure.string/replace path #" " "_"))

(defn all-files [path extensions]
  (let [path (to-folder-path path)]
    (map #(clojure.string/replace (.getAbsolutePath %)
                                  (re-quote-replacement path)
                                  "")
         (filter #(and (not (.isDirectory %))
                       (some (fn [extension]
                               (.endsWith (.getName %) (str "." extension)))
                             extensions))
                 (tree-seq (fn [f] (and (.isDirectory f) (not (.isHidden f))))
                           (fn [d] (filter #(not (.isHidden %)) (.listFiles d)))
                           (file path))))))

(defn get-group-samples [base-path group extensions]
  (let [group-path (str (to-folder-path base-path)
                        (to-folder-path group))
        files (concat (.listFiles (file group-path))
                      (.listFiles (file (str group-path "_samples"))))]
    (map #(.getName %)
         (filter #(and (not (.isDirectory %))
                       (not (.isHidden %))
                       (some (fn [extension]
                               (.endsWith (.getName %) (str "." extension)))
                             extensions))
                 files))))

(defn get-groups-from-fs [path]
  (let [path (to-folder-path path)
        fpath (file path)]
    (map #(clojure.string/replace (.getAbsolutePath %)
                                  (re-quote-replacement path)
                                  "")
         (filter #(and (.isDirectory %)
                       (not (= (.getName %) "_samples"))
                       (not (= fpath %)))
                 (tree-seq (fn [f] (and (.isDirectory f) (not (.isHidden f))))
                           (fn [d] (filter #(not (.isHidden %)) (.listFiles d)))
                           fpath)))))

(defn load-group-config [base-path group]
  (let [folder-path (str (to-folder-path base-path) (to-folder-path group))
        config-path (str folder-path "group.cfg")]
    (if (.exists (file config-path))
      (read-string (slurp config-path)))))

(defn folders [path]
  (map #(.getName %)
       (filter #(and (.isDirectory %)
                     (not (.isHidden %)))
               (.listFiles (file path)))))

(defn- create-group-info [path group]
  (info "creating group:" group path (load-group-config path group))
  (let [samples (get-group-samples path group #{"sample" "html"})
        loaded-config (load-group-config path group)]
    (merge {:index 1000
            :gallery-name (prettify-name group)
            :gallery-url (fix-url group)}
           (load-group-config path group)
           {:path group
            :hidden (or (= samples '("Coming_Soon.sample"))
                        (= group ""))
            :root (= group "")
            :name (prettify-name group)
            :samples (map #(sample-parser/parse (to-folder-path path)
                                                (to-folder-path group)
                                                %)
                          samples)})))

(defn groups [path]
  (info "searching for samples in" path)
  (sort-by (juxt :index :name)
           (cons (create-group-info path "")
                 (filter #(seq (:samples %))
                         (map #(create-group-info path %) (get-groups-from-fs path))))))

(defn samples [path]
  (mapcat :samples (groups path)))

(defn get-config [path file-name]
  (let [path (str (to-folder-path path) file-name)]
    (if (.exists (file path))
      (read-string (slurp path)))))
