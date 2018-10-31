(ns playground.data.config
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
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


(defn update-anychart-versions []
  (try
    (let [data (http/get "https://api.github.com/repos/AnyChart/AnyChart/branches?per_page=100")
          branches (json/parse-string (:body data) true)
          branches (map :name branches)]
      (reset! *anychart-versions branches))
    (catch Exception _ (reset! *anychart-versions '()))))


(defn add-anychart-versions [versions]
  (-> (concat versions @*anychart-versions)
      distinct
      utils/sort-versions))
