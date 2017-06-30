(ns playground.generator.parser.group-parser
  (:require [taoensso.timbre :refer [info error]]
            [clojure.java.io :refer [file]]
            [playground.generator.parser.sample-parser :as sample-parser]
            [clojure.string :as string]))

(defn inner-path [base-path sample-path]
  (let [in-path-with-name (subs sample-path (count base-path) (count sample-path))
        in-path (string/join "/" (butlast (string/split in-path-with-name #"/")))]
    (str in-path "/")))

(defn samples [path config samples-filter]
  (let [files (file-seq (file path))
        files* (filter #(and
                         (not (.isDirectory %))
                         (not (.isHidden %))
                         (or (.endsWith (.getName %) ".sample")
                             (.endsWith (.getName %) ".html"))
                         (if samples-filter
                           (re-find (re-pattern samples-filter) (inner-path path (.getAbsolutePath %)))
                           true))
                       files)]
    (map (fn [file] (sample-parser/parse path (inner-path path (.getAbsolutePath file)) config (.getName file))) files*)))
