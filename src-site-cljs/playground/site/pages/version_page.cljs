(ns playground.site.pages.version-page
  (:require-macros [hiccups.core :as h])
  (:require [playground.site.landing :refer [samples-per-page samples-per-block samples-per-landing
                                             change-title update-pagination
                                             init-buttons update-buttons]]
            [playground.views.sample :as sample-view]
            [playground.site.utils :as utils]
            [ajax.core :refer [GET POST]]
            [goog.dom :as dom]
            [playground.site.pages.version-page-utils :as version-page-utils]
            [clojure.string :as string]))

;;======================================================================================================================
;; Version page
;;======================================================================================================================
(def *page (atom 0))
(def *max-page (atom 0))
(def *is-end (atom false))
(def *total (atom 0))

(def *version-id (atom nil))
(def *version-name (atom nil))
(def *repo-title (atom nil))
(def *loading (atom false))

(declare load-version-samples)

(defn on-version-samples-load [data]
  (dom/removeChildren (dom/getElement "version-samples"))
  (set! (.-innerHTML (.getElementById js/document "version-samples"))
        (string/join (map #(h/html %) (sample-view/samples (:samples data)))))
  (reset! *is-end (:end data))
  (.pushState (.-history js/window) nil nil (str "?page=" (inc @*page)))
  (change-title (version-page-utils/title @*version-name @*page @*repo-title))
  (update-buttons "version-samples-prev"
                  "version-samples-next"
                  *page
                  @*is-end
                  (str "/projects/" @*repo-title "/" @*version-name "?page=")
                  load-version-samples
                  *loading)
  (update-pagination *page *max-page *loading (str "/projects/" @*repo-title "/" @*version-name "?page=") load-version-samples))


(defn load-version-samples []
  (POST "/version-samples.json"
        {:params        {:offset     (* samples-per-page @*page)
                         :version_id @*version-id}
         :handler       on-version-samples-load
         :error-handler #(utils/log "Error!" %)}))


(defn ^:export startVersionPage [_page _max-page _end _total _version-id _version-name _repo-title]
  ;(utils/log "Start Version: " _end _page _version-id _version-name _repo-title)
  (reset! *page _page)
  (reset! *max-page _max-page)
  (reset! *is-end _end)
  (reset! *total _total)
  (reset! *version-id _version-id)
  (reset! *version-name _version-name)
  (reset! *repo-title _repo-title)
  (init-buttons "version-samples-prev"
                "version-samples-next"
                *page
                load-version-samples
                *loading)
  (update-pagination *page *max-page *loading (str "/projects/" @*repo-title "/" @*version-name "?page=") load-version-samples))
