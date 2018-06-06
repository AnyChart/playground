(ns playground.utils.utils
  (:require [clojure.string :as string]))


(defn released-version? [version-key]
  (re-matches #"^\d+\.\d+\.\d+$" version-key))


(defn filter-released-versions [versions]
  (filter released-version? versions))


(defn released-8-version? [s]
  (re-matches #"^8\.\d+\.\d+$" s))


(defn filter-8-released-versions [versions]
  (filter released-8-version? versions))


(defn replace-urls [version-name scripts]
  (map (fn [script]
         (if (= script "../anychart-bundle.min.js")
           (str "https://cdn.anychart.com/js/" version-name "/anychart-bundle.min.js")
           script)) scripts))

;;======================================================================================================================
;; Constants
;;======================================================================================================================
(def ^:const domain "https://playground.anychart.com")

;; NEED to be initialized by back-end, front-end editor, all front-end pages
;; All frontend calls are incapsulated in: playground.views.common/run-js-fn
(defonce preview-prefix nil)

(defn ^:export init-preview-prefix [url-prefix]
  #?(:clj
     (alter-var-root #'preview-prefix (constantly url-prefix))
     :cljs
     (set! preview-prefix url-prefix)))

(declare name->url)

;;======================================================================================================================
;; Relative urls
;;======================================================================================================================
(defn image-name [sample]
  (str (name->url (:full-url sample)) ".png"))

(defn image-path [images-folder sample]
  (str images-folder "/" (image-name sample)))

;;======================================================================================================================
;; Relative urls
;;======================================================================================================================
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


(defn sample-url-with-version [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:version-name sample)
         "/" (:url sample))
    (if (and (:url sample) (seq (:url sample)))
      (str "/" (:url sample)
           "/" (:version sample)))))


(defn sample-editor-url [sample]
  (str (sample-url sample) "/editor"))


(defn sample-standalone-url [sample]
  (str (sample-url sample) "/view"))


(defn sample-iframe-url [sample]
  (str (sample-url sample) "/iframe"))


(defn sample-image-url [sample]
  ;(str (sample-url sample) "/preview")
  (str preview-prefix (name->url (:full-url sample)) ".png"))

;;======================================================================================================================
;; Full urls
;;======================================================================================================================
;(defn full-sample-editor-url [sample]
;  (str domain (sample-editor-url sample)))
;
;(defn full-sample-standalone-url [sample]
;  (str domain (sample-standalone-url sample)))
;
;(defn full-sample-iframe-url [sample]
;  (str domain (sample-iframe-url sample)))

(defn full-sample-image-url [sample]
  (str domain (sample-image-url sample)))

;;======================================================================================================================
;; Canonical urls
;;======================================================================================================================
(defn canonical-url [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:url sample))
    (if (and (:url sample) (seq (:url sample)))
      (str "/" (:url sample))
      "")))


(defn full-canonical-url [sample]
  (str domain (canonical-url sample)))


(defn full-canonical-url-iframe [sample]
  (str (full-canonical-url sample) "/iframe"))


(defn full-canonical-url-standalone [sample]
  (str (full-canonical-url sample) "/view"))

;;======================================================================================================================
;; Others functions
;;======================================================================================================================
(defn url [sample]
  (if (:latest sample)
    (canonical-url sample)
    (sample-url sample)))


(defn name->url [name]
  (-> name
      ; TODO: refactor with one replace
      (string/replace #"^/" "")
      (string/replace #"/" "-")
      (string/replace #", " "-")
      (string/replace #",_" "-")
      (string/replace #"," "-")
      (string/replace #" " "-")
      (string/replace #"_" "-")
      (string/replace #"\(" "")
      (string/replace #"\)" "")
      string/lower-case))


(defn embed-name [sample]
  (if (-> sample :version-id)
    (name->url (-> sample :url))
    (-> sample :url)))


(defn delete-spaces [s]
  (when (string? s)
    (string/replace s #"[ ]{2,}" " ")))


(defn strip-tags [s]
  (when (string? s)
    (-> s (string/replace #"<[^>]*>" ""))))


(defn strip-scripts [s]
  (when (string? s)
    (-> s (string/replace #"<script[^>]*>.*(?=</script)</script[^>]*>" ""))))


(defn trim [s]
  (when (string? s)
    (string/trim s)))


(defn full-strip [s]
  (when (string? s)
    (-> s
        strip-tags
        delete-spaces
        trim)))


(defn prepare-sample [sample]
  (-> sample
      (update :description (comp trim strip-scripts))
      (update :short-description (comp trim strip-tags))))


(defn reorder-list
  "For editor settings scripts and styles draggable sorting"
  [lst old-index new-index]
  (let [lst (vec lst)
        el (nth lst old-index)
        ;_ (println el)
        scripts (vec (concat (subvec lst 0 old-index) (subvec lst (inc old-index) (count lst))))
        ;_ (println scripts)
        scripts (concat (subvec scripts 0 new-index) [el] (subvec scripts new-index (count scripts)))]
    scripts))


(defn format-exception [e]
  (str e "\n\n" (apply str (interpose "\n" (.getStackTrace e)))))