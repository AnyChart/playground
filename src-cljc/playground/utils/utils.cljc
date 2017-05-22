(ns playground.utils.utils
  (:require [clojure.string :as s]))

(defn released-version? [version-key]
  (re-matches #"^\d+\.\d+\.\d+$" version-key))

(defn replace-urls [version-name scripts]
  (map (fn [script]
         (if (= script "../anychart-bundle.min.js")
           (str "https://cdn.anychart.com/js/" version-name "/anychart-bundle.min.js")
           script)) scripts))

(defn sample-url [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:version-name sample)
         "/" (:url sample))
    (if (and (:url sample) (seq (:url sample)))
      (str "/" (:url sample)
           (when (pos? (:version sample))
             (str "/" (:version sample))))
      "")))

(defn canonical-url [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:url sample))
    (if (and (:url sample) (seq (:url sample)))
      (str "/" (:url sample))
      "")))

(defn name->url [name]
  (-> name
      (clojure.string/replace #"^/" "")
      (clojure.string/replace #"/" "-")
      (clojure.string/replace #", " "-")
      (clojure.string/replace #",_" "-")
      (clojure.string/replace #"," "-")
      (clojure.string/replace #" " "-")
      (clojure.string/replace #"_" "-")
      s/lower-case))