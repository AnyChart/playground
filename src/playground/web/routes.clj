(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect response]]
            [taoensso.timbre :as timbre]
            [playground.db.request :as db-req]
            [playground.generator.core :as worker]))

(defn get-db [request] (-> request :component :db))

(defn get-generator [request] (-> request :component :generator))

(defn update-repo [project request]
  (prn "Project: " project)
  (future (worker/update-repository (get-db request) project))
  (response (str "Start updating " (:name @project))))

(defn- check-project-middleware [app]
  (fn [request]
    (let [project-key (-> request :route-params :project)
          project (worker/get-repo-by-name (get-generator request) project-key)]
      (if project
        (app project request)
        (route/not-found "project not found")))))

(defroutes app-routes
           (GET "/" [] "It works!")
           (GET "/:project/_update_" [] (check-project-middleware update-repo))
           (POST "/:project/_update_" [] (check-project-middleware update-repo))
           (route/not-found "Page not found."))