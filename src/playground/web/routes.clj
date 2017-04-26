(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect response file-response]]
            [taoensso.timbre :as timbre]
            [selmer.parser :refer [render-file]]
            [playground.db.request :as db-req]
            [playground.generator.core :as worker]
            [playground.redis.core :as redis]
            [playground.utils.utils :as common-utils]
            [playground.web.utils :as web-utils]
            [playground.views.landing-page :as landing-view]
            [playground.views.version-page :as version-view]
            [playground.views.repo-page :as repo-view]
            [playground.preview-generator.phantom :as phantom]))

(def ^:const samples-per-page 12)

(defn get-db [request] (-> request :component :db))
(defn get-redis [request] (-> request :component :redis))
(defn get-redis-queue [request] (-> (get-redis request) :config :queue))

(defn update-repo [repo request]
  (redis/enqueue (get-redis request)
                 (get-redis-queue request)
                 (:name repo))
  (response (str "Start updating: " (:name repo))))


(defn landing-page [request]
  (prn request)
  (let [page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
        samples (db-req/top-samples (get-db request) {:count  (inc samples-per-page)
                                                      :offset (* samples-per-page page)})]
    (prn page)
    (landing-view/page {:samples   (take samples-per-page samples)
                        :end       (< (count samples) (inc samples-per-page))
                        :page      page
                        :templates (-> request :app :templates)
                        :repos     (-> request :app :repos)})))
(defn show-sample-iframe [sample request]
  (response (render-file "templates/sample.selmer" sample)))

(defn show-sample-standalone [sample request]
  (db-req/update-sample-views! (get-db request) {:id (:id sample)})
  (let [templates (db-req/templates (get-db request))]
    (render-file "templates/standalone-page.selmer" {:sample    sample
                                                     :templates templates
                                                     :url       (str (common-utils/sample-url sample)
                                                                     "?view=iframe")})))

(defn show-sample-editor [sample request]
  (db-req/update-sample-views! (get-db request) {:id (:id sample)})
  (let [templates (db-req/templates (get-db request))]
    (render-file "templates/editor.selmer" {:data (web-utils/pack {:sample    sample
                                                                   :templates templates})})))

(defn show-sample-preview [sample request]
  (if (:preview sample)
    (file-response (phantom/image-path (-> request :component :conf :images-dir) sample))
    (response "Preview is not available, try later.")))

(defn show-sample-by-view [view sample request]
  (case view
    "standalone" (show-sample-standalone sample request)
    "iframe" (show-sample-iframe sample request)
    "editor" (show-sample-editor sample request)
    "preview" (show-sample-preview sample request)
    nil (show-sample-editor sample request)))

(defn show-sample [repo version sample request]
  (let [view (-> request :params :view)]
    (show-sample-by-view view sample request)))

(defn show-user-sample [request]
  ;(prn "Show user sample: " (-> request :route-params))
  (let [hash (-> request :route-params :hash)
        version (or (some-> (-> request :route-params :version) Integer.) 0)
        sample (db-req/sample-by-hash (get-db request) {:url     hash
                                                        :version version})
        view (-> request :params :view)]
    (when sample
      (show-sample-by-view view sample request))))

;; middleware for getting repo, version, sample
(defn- check-repo-middleware [handler]
  (fn [request]
    (let [repo-name (-> request :route-params :repo)
          repo (db-req/repo-by-name (get-db request) {:name repo-name})]
      (when repo
        (handler repo request)))))

(defn- check-version-middleware [handler]
  (fn [repo request]
    (let [version-name (-> request :route-params :version)
          version (db-req/version-by-name (get-db request) {:repo-id (:id repo)
                                                            :name    version-name})]
      (when version
        (handler repo version request)))))

(defn- check-sample-middleware [handler]
  (fn [repo version request]
    (let [sample-url (-> request :route-params :*)
          sample (db-req/sample-by-url (get-db request) {:version-id (:id version)
                                                         :url        sample-url})]
      (when sample
        (handler repo version (-> sample
                                  (assoc :repo-name (:name repo))
                                  (assoc :version-name (:name version))
                                  db-req/add-full-url) request)))))

(defn- templates-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :templates]
                       (db-req/templates (get-db request))))))

(defn- repos-middleware [handler]
  (fn [request]
    (handler (assoc-in request [:app :repos]
                       (db-req/repos (get-db request))))))

(defn repo-page [repo request]
  (let [versions (db-req/versions (get-db request) {:repo-id (:id repo)})
        versions-with-samples (filter (comp pos? :samples-count) versions)]
    ;(render-file "templates/repo-page.selmer" {:repo      repo
    ;                                           :templates (-> request :app :templates)
    ;                                           :repos     (-> request :app :repos)
    ;                                           :versions  versions-with-samples})
    (repo-view/page {:repo      repo
                     :templates (-> request :app :templates)
                     :repos     (-> request :app :repos)
                     :versions  versions-with-samples})))

(defn version-page [repo version request]
  (let [page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
        samples (db-req/samples-by-version (get-db request) {:version_id (:id version)
                                                             :offset     (* samples-per-page page)
                                                             :count      (inc samples-per-page)})]
    (version-view/page {:samples   (take samples-per-page samples)
                        :end       (< (count samples) (inc samples-per-page))
                        :page      page
                        :version   version
                        :repo      repo
                        :templates (-> request :app :templates)
                        :repos     (-> request :app :repos)})))

(defn top-landing-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        samples (db-req/top-samples (get-db request) {:count  (inc samples-per-page)
                                                      :offset offset})
        result {:samples (take samples-per-page samples)
                :end     (< (count samples) (inc samples-per-page))}]
    (response result)))

(defn top-version-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        version-id (-> request :params :version_id)
        samples (db-req/samples-by-version (get-db request) {:version_id version-id
                                                             :count      (inc samples-per-page)
                                                             :offset     offset})
        result {:samples (take samples-per-page samples)
                :end     (< (count samples) (inc samples-per-page))}]
    ;(prn "version samples: " offset version-id (count samples))
    (response result)))

(def empty-sample
  {:name              ""
   :tags              []
   :short-description ""
   :description       ""
   :url               ""

   :styles            []
   :scripts           []

   :markup            ""
   :markup-type       "html"

   :code              ""
   :code-type         "js"

   :style             ""
   :style-type        "css"})

(defn new [request]
  (let [template-url (-> request :params :template)
        view (-> request :params :view)
        sample (if template-url
                 (db-req/template-by-url (get-db request) {:url template-url})
                 empty-sample)
        sample* (assoc sample :new true)]
    ;(prn "New: " template-url view sample)
    (show-sample-by-view view sample* request)))

(defn run [request]
  ;(prn "run: " (:params request))
  (let [code (-> request :params :code)
        style (-> request :params :style)
        markup (-> request :params :markup)
        styles (-> request :params :styles (clojure.string/split #","))
        scripts (-> request :params :scripts (clojure.string/split #","))]
    (response (render-file "templates/sample.selmer" {:name              "Default name"
                                                      :tags              []
                                                      :short-description "Default short desc"

                                                      :scripts           scripts
                                                      :styles            styles

                                                      :markup            markup
                                                      :code              code
                                                      :style             style}))))

(defn fork [request]
  (prn "Fork: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (web-utils/new-hash)
        sample* (assoc sample
                  :url hash
                  :version 0)]
    (prn "Fork: " sample*)
    (let [id (db-req/add-sample! (get-db request) sample*)]
      (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
    (response {:status  :ok
               :hash    hash
               :version 0})))

(defn save [request]
  (prn "Save: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (:url sample)
        db-sample (when (and hash (seq hash))
                    (db-req/sample-template-by-url (get-db request) {:url hash}))]
    ;(prn "db sample: " db-sample)
    ;(prn "db sample: " (:template-id db-sample))
    ;(prn "db sample: " (:version-id db-sample))
    (if (and db-sample
             (nil? (:template-id db-sample))
             (nil? (:version-id db-sample)))
      (let [
            ;version (db-req/sample-version (get-db request) {:url hash}) ;; TODO transaction
            version (:version db-sample)
            sample* (assoc sample :version (inc version))]
        (prn "Save, insert" sample*)
        (let [id (db-req/add-sample! (get-db request) sample*)]
          (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
        (response {:status  :ok
                   :hash    hash
                   :version (inc version)}))
      (fork request))))

(defn- generate-previews [samples request]
  (let [ids (map :id samples)]
    (if (seq ids)
      (do (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) ids)
          (response (str "Start generate previews for " (count samples) " samples: " (clojure.string/join ", " (map :name samples)))))
      "All samples have previews")))

(defn user-previews [request]
  (generate-previews (db-req/user-samples-without-preview (get-db request)) request))

(defn repo-previews [request]
  (generate-previews (db-req/repo-samples-without-preview (get-db request)) request))

(defroutes app-routes
           (route/resources "/")
           (GET "/" [] (repos-middleware
                         (templates-middleware
                           landing-page)))
           (GET "/signin" [] "signin")
           (GET "/signup" [] "signup")

           (GET "/new" [] new)
           (POST "/run" [] run)
           (POST "/save" [] save)
           (POST "/fork" [] fork)

           (GET "/_user_previews_" [] user-previews)
           (GET "/_repo_previews_" [] repo-previews)

           (GET "/:repo/_update_" [] (check-repo-middleware
                                       update-repo))
           (POST "/:repo/_update_" [] (check-repo-middleware
                                        update-repo))

           (GET "/landing-samples.json" [] top-landing-samples)
           (POST "/landing-samples.json" [] top-landing-samples)
           (GET "/version-samples.json" [] top-version-samples)
           (POST "/version-samples.json" [] top-version-samples)

           (GET "/:repo" [] (repos-middleware
                              (templates-middleware
                                (check-repo-middleware
                                  repo-page))))
           (GET "/:repo/" [] (fn [request]
                               (when ((repos-middleware
                                        (templates-middleware
                                          (check-repo-middleware
                                            repo-page))) request)
                                 (redirect (web-utils/drop-slash (:uri request)) 301))))

           (GET "/:repo/:version" [] (repos-middleware
                                       (templates-middleware
                                         (check-repo-middleware
                                           (check-version-middleware
                                             version-page)))))
           (GET "/:repo/:version/" [] (fn [request]
                                        (when ((repos-middleware
                                                 (templates-middleware
                                                   (check-repo-middleware
                                                     (check-version-middleware
                                                       version-page)))) request)
                                          (redirect (web-utils/drop-slash (:uri request)) 301))))

           (GET "/:repo/:version/*" [] (check-repo-middleware
                                         (check-version-middleware
                                           (check-sample-middleware
                                             show-sample))))

           (GET "/:hash/" [] show-user-sample)
           (GET "/:hash" [] show-user-sample)
           (GET "/:hash/:version" [] show-user-sample)
           (GET "/:hash/:version/" [] show-user-sample)
           (route/not-found "404 Page not found"))