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
    ;; views
            [playground.views.editor.editor-page :as editor-view]
            [playground.views.iframe :as iframe-view]
    ;; misc
            [hiccup.core :as hiccup]))

;; =====================================================================================================================
;; Samples pages handlers
;; =====================================================================================================================
(defn show-sample-editor [request & [editor-view]]
  (let [sample (get-sample request)
        ;; Not need to get them in aggregations function via middleware, cause iframe-view e.g. doesn't need them
        templates (db-req/templates (get-db request))
        data-sets (db-req/data-sets (get-db request))
        data {:canonical-url (if editor-view
                               (utils/full-canonical-url-standalone sample)
                               (utils/full-canonical-url sample))
              :sample        sample
              :data          (web-utils/pack {:sample    sample
                                              :templates templates
                                              :datasets  (map #(dissoc % :data) data-sets)
                                              :user      (get-safe-user request)
                                              :view      editor-view})}]
    (db-req/update-sample-views! (get-db request) {:id (:id sample)})
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
      (file-response (phantom/image-path (-> request :component :conf :images-dir) sample))
      (response "Preview is not available, try later."))))

(defn show-sample-download [request]
  (assoc (show-sample-iframe request)
    :headers {"Content-Disposition" (str "attachment; filename=\"" (:name (get-sample request)) ".html\"")}))