(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect response]]
            [taoensso.timbre :as timbre]
            [selmer.parser :refer [render-file]]
            [playground.db.request :as db-req]
            [playground.generator.core :as worker]
            [playground.redis.core :as redis]
            [playground.utils.utils :as common-utils]
            [playground.web.utils :as web-utils]))

(defn get-db [request] (-> request :component :db))
(defn get-redis [request] (-> request :component :redis))
(defn get-redis-queue [request] (-> (get-redis request) :config :queue))

(defn landing-page [request]
  (let [samples (db-req/top-samples (get-db request) {:count 9})]
    (render-file "templates/landing-content.selmer" {:samples samples})))

(defn update-repo [repo request]
  (prn "Repo: " repo)
  (redis/enqueue (get-redis request)
                 (get-redis-queue request)
                 (:name repo))
  (response (str "Start updating: " (:name repo))))


(defn show-sample-iframe [repo version sample request]
  (response (render-file "templates/sample.selmer" sample)))

(defn show-sample-standalone [repo version sample]
  (render-file "templates/standalone-iframe-content.selmer" {:sample sample
                                                             :url    (str (common-utils/sample-url sample)
                                                                          "?view=iframe")}))

(defn show-sample-editor [repo version sample request]
  ;(prn (web-utils/pack sample))
  (render-file "templates/editor.selmer" {:data (web-utils/pack sample)}))

(defn show-sample [repo version sample request]
  (let [view (-> request :params :view)]
    (case view
      "standalone" (show-sample-standalone repo version sample)
      "iframe" (show-sample-iframe repo version sample request)
      "editor" (show-sample-editor repo version sample request)
      nil (show-sample-editor repo version sample request)
      "Bad view type")))

(defn show-user-sample [request]
  (prn "Show user sample: " (-> request :route-params))
  (let [hash (-> request :route-params :hash)
        version (or (some-> (-> request :route-params :version) Integer.) 0)
        sample (db-req/sample-by-hash (get-db request) {:url     hash
                                                        :version version})
        view (-> request :params :view)]
    (if sample
      (case view
        "standalone" (show-sample-standalone nil nil sample)
        "iframe" (show-sample-iframe nil nil sample request)
        "editor" (show-sample-editor nil nil sample request)
        nil (show-sample-editor nil nil sample nil))
      (route/not-found "sample not found"))))

;; middleware for getting repo, version, sample
(defn- check-repo-middleware [handler]
  (fn [request]
    (let [repo-name (-> request :route-params :repo)
          repo (db-req/repo-by-name (get-db request) {:name repo-name})]
      (if repo
        (handler repo request)
        (route/not-found "repo not found")))))

(defn- check-version-middleware [handler]
  (fn [repo request]
    (let [version-name (-> request :route-params :version)
          version (db-req/version-by-name (get-db request) {:repo_id (:id repo)
                                                            :name    version-name})]
      (if version
        (handler repo version request)
        (route/not-found "version not found")))))

(defn- check-sample-middleware [handler]
  (fn [repo version request]
    (let [sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version_id (:id version)
                                                         :url        (str "/" sample-url)})]
      (if sample
        (handler repo version (assoc sample
                                :repo-name (:name repo)
                                :version-name (:name version)) request)
        (route/not-found "sample not found")))))

(defn new [request]
  "New chart")

(defn run [request]
  (prn "run: " (:params request))
  (let [code (-> request :params :code)
        style (-> request :params :style)
        markup (-> request :params :markup)
        styles (-> request :params :styles (clojure.string/split #","))
        scripts (-> request :params :scripts (clojure.string/split #","))]
    (response (render-file "templates/sample.selmer" {:name              "Default name"
                                                      :tags              []
                                                      :short_description "Default short desc"

                                                      :scripts           scripts
                                                      :styles            styles

                                                      :markup            markup
                                                      :code              code
                                                      :style             style}))))

(defn save [request]
  (prn "Save: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (:url sample)
        ;; TODO transaction
        version (db-req/sample-version (get-db request) {:url hash})
        sample* (assoc sample :version (inc version))
        ]
    (prn "Save, insert" sample*)
    (db-req/add-sample! (get-db request) sample*)
    (response {:status  :ok
               :hash    hash
               :version (inc version)})))

(defn fork [request]
  (prn "Fork: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (web-utils/new-hash)
        sample* (assoc sample
                  :url hash
                  :version 0)]
    (prn "Fork: " sample*)
    (db-req/add-sample! (get-db request) sample*)
    (response {:status  :ok
               :hash    hash
               :version 0})))


(defroutes app-routes
           (route/resources "/")
           (GET "/" [] landing-page)
           (GET "/signin" [] "signin")
           (GET "/signup" [] "signup")

           (GET "/new" [] new)
           (POST "/run" [] run)
           (POST "/save" [] save)
           (POST "/fork" [] fork)

           (GET "/:repo/_update_" [] (check-repo-middleware update-repo))
           (POST "/:repo/_update_" [] (check-repo-middleware update-repo))

           (GET "/:hash/" [] show-user-sample)
           (GET "/:hash" [] show-user-sample)
           (GET "/:hash/:version" [] show-user-sample)
           (GET "/:hash/:version/" [] show-user-sample)
           (GET "/:repo/:version/*" [] (check-repo-middleware
                                         (check-version-middleware
                                           (check-sample-middleware
                                             show-sample))))

           ;(GET "/sample/*-iframe" [] show-sample-iframe)
           (route/not-found "404 Page not found"))