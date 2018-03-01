(ns playground.web.handlers.sample.pages
  (:require [compojure.core :refer :all]
    ;; components
            [playground.db.request :as db-req]
            [playground.utils.utils :as utils]
            [playground.preview-generator.phantom :as phantom]
    ;; web
            [ring.util.response :refer [redirect file-response content-type]]
            [playground.web.utils :as web-utils :refer [response]]
            [playground.web.helpers :refer :all]
            [playground.web.handlers.sample.zip :as zip]
    ;; views
            [playground.views.editor.editor-page :as editor-view]
            [playground.views.iframe :as iframe-view]
    ;; misc
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [taoensso.timbre :as timbre]))

;; =====================================================================================================================
;; Samples pages handlers
;; =====================================================================================================================
(defn show-sample-editor [request & [editor-view]]
  (let [sample (get-sample request)
        user (get-user request)
        ;; Not need to get them in aggregations function via middleware, cause iframe-view e.g. doesn't need them
        templates (db-req/templates (get-db request))
        data-sets (db-req/data-sets (get-db request))
        embed-show (or (contains? (-> request :params) :export)
                       (= "export" (-> request :query-string)))
        data {:canonical-url (if editor-view
                               (utils/full-canonical-url-standalone sample)
                               (utils/full-canonical-url sample))
              :sample        sample
              :data          (web-utils/pack {:sample     sample
                                              :templates  templates
                                              :datasets   (map #(dissoc % :data) data-sets)
                                              :user       (get-safe-user request)
                                              :view       editor-view
                                              :embed-show embed-show})}]
    ;; when not "new" sample
    (when (:id sample)
      (jdbc/with-db-transaction [conn (:db-spec (get-db request))]
                                (when-not (db-req/get-visit conn {:sample-id (:id sample)
                                                                  :user-id   (:id user)})
                                  (db-req/visit! conn {:sample-id (:id sample)
                                                       :user-id   (:id user)}))
                                (when-not (db-req/get-canonical-visit conn {:user-id (:id user)
                                                                            :url     (:url sample)
                                                                            :repo-id (:repo-id sample)})
                                  (db-req/canonical-visit! conn {:user-id (:id user)
                                                                 :url     (:url sample)
                                                                 :repo-id (:repo-id sample)}))
                                (db-req/update-sample-views-from-canonical-visits! conn {:url     (:url sample)
                                                                                         :repo-id (:repo-id sample)})))

    (response (editor-view/page data))))


(defn show-sample-standalone [request]
  (show-sample-editor request :standalone))


(defn show-sample-iframe-by-sample [sample]
  (response (str "<!DOCTYPE html>\n"
                 (hiccup/html (iframe-view/iframe sample)))))


(defn show-sample-iframe [request]
  (show-sample-iframe-by-sample (get-sample request)))


(defn show-sample-preview [request]
  (let [sample (get-sample request)]
    (if (:preview sample)
      (do
        ;; TODO: delete logging
        (timbre/warn "Image from Clojure server: " (utils/image-path (-> request :component :conf :images-dir) sample))
        (file-response (utils/image-path (-> request :component :conf :images-dir) sample)))
      (response "Preview is not available, try later."))))


(defn show-sample-download [request]
  (assoc (show-sample-iframe request)
    :headers {"Content-Disposition" (str "attachment; filename=\"" (:name (get-sample request)) ".html\"")}))


(defn show-sample-download-zip [request]
  ;(response (zip/download request))
  (response "Under development")
  )


(defn sample-data [request]
  (response (get-sample request)))


;; TODO: redirects for group, delete in 6-9 months
(defn group-redirect [request]
  (let [group (-> request :params :group)
        version-id (:id (get-version request))
        samples-urls (db-req/group-samples (get-db request) {:version_id version-id
                                                             :url        group})]
    (when (seq samples-urls)
      (redirect (str "/"
                     (:name (get-repo request)) "/"
                     (:name (get-version request)) "/"
                     (:url (first samples-urls))) 301))))