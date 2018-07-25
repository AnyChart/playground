(ns playground.web.handlers.admin-handlers
  (:require [playground.views.admin.admin-page :as admin-view]
            [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]))


(defn page [request]
  (let [data (:app request)
        ;repos (db-req/versions-repos (get-db request))
        ]
    (admin-view/page data)))

;; API
(defn versions [request]
  (let [project-id (-> request :params :project-id)
        versions (db-req/versions (get-db request) {:repo-id project-id})]
    versions))


(defn delete-version [request]
  (let [version (-> request :params :version)]
    (println version)

    "ok")
  )