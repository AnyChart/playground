(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect response]]
            [taoensso.timbre :as timbre]
            [selmer.parser :refer [render-file]]
            [playground.db.request :as db-req]
            [playground.generator.core :as worker]
            [playground.utils.utils :as utils]))

(defn get-db [request] (-> request :component :db))

(defn get-generator [request] (-> request :component :generator))

(defn landing-page [request]
  (let [samples (db-req/top-samples (get-db request) {:count 9})]
    (prn "samples: " samples)
    (render-file "templates/landing.selmer" {:samples samples})))

(defn editor-page [request]
  (render-file "templates/index.selmer" {}))

(defn update-repo [project request]
  (prn "Project: " project)
  (future (worker/update-repository (get-generator request) (get-db request) project))
  (response (str "Start updating " (:name @project))))


(defn show-sample-iframe [proejct version sample request]
  (let [need-anychart-script (utils/need-anychart-script? (:scripts sample))
        styles (utils/csss (:styles sample) (:key version) need-anychart-script)]
    (response (render-file "templates/sample.selmer" (assoc sample
                                                      :anychart-need true
                                                      :anychart-url (utils/anychart-bundle-url (:key version))
                                                      :styles styles)))))


;; middleware for getting project, version, sample
(defn- check-project-middleware [handler]
  (fn [request]
    (let [project-key (-> request :route-params :project)
          project (worker/get-repo-by-name (get-generator request) project-key)]
      (if project
        (handler project request)
        (route/not-found "project not found")))))

(defn- check-version-middleware [handler]
  (fn [project request]
    (let [version-key (-> request :route-params :version)
          version (db-req/version-by-name (get-db request) {:project_id (:id @project)
                                                            :key        version-key})]
      (if version
        (handler project version request)
        (route/not-found "version not found")))))

(defn- check-sample-middleware [handler]
  (fn [project version request]
    (let [sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version_id (:id version)
                                                         :url        (str "/" sample-url)})]
      (if sample
        (handler project version sample request)
        (route/not-found "sample not found")))))

(defroutes app-routes
           (route/resources "/")
           (GET "/" [] landing-page)
           (GET "/signin" [] "signin")
           (GET "/signup" [] "signup")
           (GET "/editor" [] editor-page)
           (GET "/:project/_update_" [] (check-project-middleware update-repo))
           (POST "/:project/_update_" [] (check-project-middleware update-repo))
           (GET "/:project/:version/*-iframe" [] (check-project-middleware (check-version-middleware (check-sample-middleware show-sample-iframe))))
           (GET "/sample/*-iframe" [] show-sample-iframe)
           (route/not-found "Page not found."))