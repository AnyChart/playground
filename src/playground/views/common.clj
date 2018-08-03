(ns playground.views.common
  (:require [playground.data.tags :as tags-data]
            [playground.data.config :as c]
            [playground.web.auth-base :as auth-base]
            [playground.utils.utils :as utils]
    ;; html markup components
            [playground.views.common.footer :as footer-comp]
            [playground.views.common.create-buttons :as create-buttons-comp]
            [playground.views.common.navigator :as navigator-comp]
            [playground.views.common.head :as head-comp]
            [playground.views.common.resources :as resources]
    ;; utils
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clj-time.core :as t])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))


;; =====================================================================================================================
;; Resources
;; =====================================================================================================================
(def head-tag-manager resources/head-tag-manager)

(def body-tag-manager resources/body-tag-manager)


;; Scripts
(def site-script resources/site-script)

(def jquery-script resources/jquery-script)

(def bootstrap-script resources/bootstrap-script)


;; =====================================================================================================================
;; Helper functions
;; =====================================================================================================================
(defn desc [text]
  (when (seq text)
    (let [text (-> text
                   (string/replace #"<br/>" " ")
                   (string/replace #"<[^>]*>" ""))
          words (string/split (subs text 0 (min (count text) 160)) #" ")
          result (reduce (fn [res part]
                           (if (empty? res)
                             part
                             (if (< (count (str res " " part)) 155)
                               (str res " " part)
                               res))) "" words)]
      (string/trim result))))


(defn run-js-fn [fn-name & params]
  (str "playground.utils.utils.init_preview_prefix('" utils/preview-prefix "');"
       fn-name "("
       (->> params
            (map #(if (string? %)
                    (str "\"" (StringEscapeUtils/escapeJson %) "\"")
                    %))
            (string/join ","))
       ");"))


(defn run-js-fns [& fns]
  (->> fns
       (map #(apply run-js-fn %))
       (string/join "\n")))


(defn search-query [{:keys [repo version tag]}]
  (str
    (when repo (str "p:" repo) " ")
    (when version (str "v:" version) " ")
    (when tag (if (string/includes? tag " ")
                (str "t:'" tag "' ")
                (str "t:" tag " ")))))


;; =====================================================================================================================
;; Facade
;; =====================================================================================================================
(defn head [data]
  (head-comp/head data))


(defn nav [templates user & [q]]
  (navigator-comp/nav templates user q))


(defn create-box [templates]
  (create-buttons-comp/create-box templates))


(defn footer [repos tags data-sets]
  (footer-comp/footer repos tags data-sets))


(defn bottom-footer []
  (footer-comp/bottom-footer))