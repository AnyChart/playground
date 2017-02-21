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
    (render-file "templates/landing.selmer" {:samples samples})))

(defn editor-page [request]
  (render-file "templates/index.selmer" {}))

(defn update-repo [repo request]
  (prn "Repo: " repo)
  (future (worker/update-repository (get-generator request) (get-db request) repo))
  (response (str "Start updating " (:name @repo))))


(defn show-sample-iframe [repo version sample request]
  (let [need-anychart-script (utils/need-anychart-script? (:scripts sample))
        styles (utils/csss (:styles sample) (:name version) need-anychart-script)]
    (response (render-file "templates/sample.selmer" (assoc sample
                                                      :anychart-need true
                                                      :anychart-url (utils/anychart-bundle-url (:name version))
                                                      :styles styles)))))


;; middleware for getting repo, version, sample
(defn- check-repo-middleware [handler]
  (fn [request]
    (let [repo-name (-> request :route-params :repo)
          repo (worker/get-repo-by-name (get-generator request) repo-name)]
      (if repo
        (handler repo request)
        (route/not-found "repo not found")))))

(defn- check-version-middleware [handler]
  (fn [repo request]
    (let [version-name (-> request :route-params :version)
          version (db-req/version-by-name (get-db request) {:repo_id (:id @repo)
                                                            :name        version-name})]
      (if version
        (handler repo version request)
        (route/not-found "version not found")))))

(defn- check-sample-middleware [handler]
  (fn [repo version request]
    (let [sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version_id (:id version)
                                                         :url        (str "/" sample-url)})]
      (if sample
        (handler repo version sample request)
        (route/not-found "sample not found")))))

(defroutes app-routes
           (route/resources "/")
           (GET "/" [] landing-page)
           (GET "/signin" [] "signin")
           (GET "/signup" [] "signup")
           (GET "/editor" [] editor-page)
           (GET "/:repo/_update_" [] (check-repo-middleware update-repo))
           (POST "/:repo/_update_" [] (check-repo-middleware update-repo))
           (GET "/:repo/:version/*-iframe" [] (check-repo-middleware (check-version-middleware (check-sample-middleware show-sample-iframe))))
           (GET "/sample/*-iframe" [] show-sample-iframe)
           (route/not-found "Page not found."))