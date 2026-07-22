(ns playground.data.config
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.string :as string]
            [playground.utils.utils :as utils]))


;; Declarations
(declare update-anychart-versions)


;; Config
(defonce data nil)

(defn set-config [conf]
  (alter-var-root (var data) (constantly conf))
  (update-anychart-versions))


;; Config getters
(defn prefix [] (-> data :common :prefix))

(defn domain [] (-> data :common :domain))

(defn commit [] (:commit data))

(defn repos-for-versions [] (-> data :editor :repos-for-versions))

(defn released-versions [] (-> data :editor :released-versions))



;; AnyChart versions to show in editor's select on .stg for developers
(defonce *anychart-versions (atom []))


(defn- gh-names
  "Fetch the :name field from a GitHub list endpoint (branches/tags)."
  [url]
  (->> (http/get url) :body (#(json/parse-string % true)) (map :name)))


(defn- tag->version
  "Strip the leading v from semver release tags (v8.14.1 -> 8.14.1) so the
   cdn.anychart.com/releases/<v>/ URLs resolve; leave aliases like v7/v8 as-is."
  [name]
  (string/replace name #"^v(\d+\.\d+\.\d+)$" "$1"))


(defn update-anychart-versions []
  ;; ACWEB-6: include release TAGS (v8.8.0..v8.14.1), not just branches, so the
  ;; editor's AnyChart-version selector shows the full release list. Tags are
  ;; v-prefixed but CDN release paths are bare, so strip the v from semver tags.
  ;; NOTE: per_page=100 single page (engine has ~24 branches / ~50 tags); add
  ;; pagination if either ever exceeds 100.
  (try
    (reset! *anychart-versions
            (distinct
              (concat
                (gh-names "https://api.github.com/repos/AnyChart/AnyChart/branches?per_page=100")
                (map tag->version
                     (gh-names "https://api.github.com/repos/AnyChart/AnyChart/tags?per_page=100")))))
    (catch Exception _ (reset! *anychart-versions '()))))


(defn add-anychart-versions [versions]
  (-> (concat versions @*anychart-versions)
      distinct
      utils/sort-versions))
