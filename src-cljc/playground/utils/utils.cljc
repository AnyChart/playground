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

(defn sample-editor-url [sample]
  (str (sample-url sample) "/editor"))

(defn sample-standalone-url [sample]
  (str (sample-url sample) "/view"))

(defn sample-iframe-url [sample]
  (str (sample-url sample) "/iframe"))

(defn canonical-url [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:url sample))
    (if (and (:url sample) (seq (:url sample)))
      (str "/" (:url sample))
      "")))

(defn full-canonical-url [sample]
  (str "https://pg.anychart.com" (canonical-url sample)))

(defn full-canonical-url-iframe [sample]
  (str (full-canonical-url sample) "/iframe"))

(defn full-canonical-url-standalone [sample]
  (str (full-canonical-url sample) "/view"))

(defn url [sample]
  (if (:latest sample)
    (canonical-url sample)
    (sample-url sample)))

(defn name->url [name]
  (-> name
      ; TODO: refactor with one replace
      (clojure.string/replace #"^/" "")
      (clojure.string/replace #"/" "-")
      (clojure.string/replace #", " "-")
      (clojure.string/replace #",_" "-")
      (clojure.string/replace #"," "-")
      (clojure.string/replace #" " "-")
      (clojure.string/replace #"_" "-")
      (clojure.string/replace #"\(" "")
      (clojure.string/replace #"\)" "")
      s/lower-case))

(defn embed-name [sample]
  (if (-> sample :version-id)
    (name->url (-> sample :url))
    (-> sample :url)))