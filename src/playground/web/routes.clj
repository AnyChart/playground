(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect file-response]]
            [taoensso.timbre :as timbre]
            [selmer.parser :refer [render-file]]
            [crypto.password.bcrypt :as bcrypt]
    ;; components
            [playground.db.request :as db-req]
            [playground.generator.core :as worker]
            [playground.redis.core :as redis]
            [playground.utils.utils :as common-utils]
            [playground.preview-generator.phantom :as phantom]
    ;; web
            [playground.web.utils :as web-utils :refer [response]]
            [playground.web.auth :as auth]
            [playground.web.middleware :as mw]
            [playground.web.auth-base :as auth-base]
            [playground.web.helpers :refer :all]
            [playground.web.chartopedia :as chartopedia]
            [playground.tags :as tags-data]
    ;; pages
            [playground.views.landing-page :as landing-view]
            [playground.views.register-page :as register-view]
            [playground.views.auth-page :as auth-view]
            [playground.views.profile-page :as profile-view]

            [playground.views.repo.repo-page :as repo-view]
            [playground.views.repo.repos-page :as repos-view]
            [playground.views.repo.version-page :as version-view]

            [playground.views.tag.tags-page :as tags-view]
            [playground.views.tag.tag-page :as tag-view]

            [playground.views.marketing.chart-type.chart-types-page :as chart-types-view]
            [playground.views.marketing.chart-type.chart-type-page :as chart-type-view]
            [playground.views.marketing.chart-type.chart-types-categories-page :as chart-type-categories-view]
            [playground.views.marketing.chart-type.chart-types-category-page :as chart-type-category-view]

            [playground.views.marketing.data-set.data-sets-page :as data-sets-view]
            [playground.views.marketing.data-set.data-set-page :as data-set-view]

            [playground.views.standalone-sample-page :as standalone-sample-view]
            [playground.views.marketing.about-page :as about-view]
            [playground.views.marketing.pricing-enterprise-page :as pricing-enterprise-view]
            [playground.views.marketing.pricing-page :as pricing-view]
            [playground.views.marketing.roadmap-page :as roadmap-view]
            [playground.views.marketing.support-page :as support-view]
            [playground.views.marketing.version-history-page :as version-history-view]
            [playground.views.iframe :as iframe-view]
            [playground.utils.utils :as utils]
            [hiccup.core :as hiccup]
            [hiccup.page :as hiccup-page]))

;; =====================================================================================================================
;; Consts
;; =====================================================================================================================
(def ^:const samples-per-page 12)
(def ^:const samples-per-block 6)

;; =====================================================================================================================
;; Show samples
;; =====================================================================================================================
(defn show-sample-iframe [sample request]
  ;(response (render-file "templates/sample.selmer" sample))
  (response (str "<!DOCTYPE html>\n"
                 (hiccup/html (iframe-view/iframe sample)))))

(defn show-sample-standalone [sample request]
  (db-req/update-sample-views! (get-db request) {:id (:id sample)})
  (let [templates (db-req/templates (get-db request))
        data-sets (db-req/data-sets (get-db request))]
    ;; site page
    ;(response (standalone-sample-view/page (merge (get-app-data request)
    ;                                              {:templates templates
    ;                                               :sample    sample
    ;                                               :url       (str (common-utils/sample-url sample) "?view=iframe")})))
    (response (render-file "templates/editor.selmer" {:canonical-url (utils/canonical-url sample)
                                                      :data          (web-utils/pack {:sample    sample
                                                                                      :templates templates
                                                                                      :data-sets data-sets
                                                                                      :user      (get-safe-user request)
                                                                                      :view      :standalone})}))))

(defn show-sample-editor [sample request]
  (db-req/update-sample-views! (get-db request) {:id (:id sample)})
  (let [templates (db-req/templates (get-db request))
        data-sets (db-req/data-sets (get-db request))]
    (response (render-file "templates/editor.selmer" {:canonical-url (utils/canonical-url sample)
                                                      :data          (web-utils/pack {:sample    sample
                                                                                      :templates templates
                                                                                      :data-sets data-sets
                                                                                      :user      (get-safe-user request)})}))))

(defn show-sample-preview [sample request]
  (if (:preview sample)
    (file-response (phantom/image-path (-> request :component :conf :images-dir) sample))
    (response "Preview is not available, try later.")))

(defn download [resp sample]
  (assoc resp :headers {"Content-Disposition" (str "attachment; filename=\"" (:name sample) ".html\"")}))

(defn show-sample-by-view [view sample request]
  (case view
    :standalone (show-sample-standalone sample request)
    :iframe (show-sample-iframe sample request)
    :editor (show-sample-editor sample request)
    :preview (show-sample-preview sample request)
    :download (download (show-sample-iframe sample request) sample)
    nil (show-sample-editor sample request)))

(defn show-sample [request]
  ;(prn "show sample: " (-> request :session))
  (let [sample (get-sample request)
        view (-> request :params :view keyword)]
    (show-sample-by-view view sample request)))

(defn show-user-sample [request]
  (let [hash (-> request :route-params :hash)
        version (try (-> request :route-params :version Integer/parseInt) (catch Exception _ 0))
        sample (db-req/sample-by-hash (get-db request) {:url     hash
                                                        :version version})
        view (-> request :params :view keyword)]
    (when sample
      (show-sample-by-view view sample request))))

(defn show-last-user-sample [request]
  (let [hash (-> request :route-params :hash)
        sample (db-req/sample-template-by-url (get-db request) {:url hash})
        view (-> request :params :view keyword)]
    (when sample
      (show-sample-by-view view sample request))))

;; =====================================================================================================================
;; Pages
;; =====================================================================================================================
(defn landing-page [request]
  ;(prn "landing: " (get-user request))
  (let [samples-page (dec (try (-> request :params :samples Integer/parseInt) (catch Exception _ 1)))
        tags-page (dec (try (-> request :params :tags Integer/parseInt) (catch Exception _ 1)))
        samples (db-req/top-samples (get-db request) {:count  (inc samples-per-block)
                                                      :offset (* samples-per-block samples-page)})
        tags-samples (db-req/get-top-tags-samples (get-db request) {:count  (inc samples-per-block)
                                                                    :offset (* samples-per-block tags-page)})]
    (response (landing-view/page (merge (get-app-data request)
                                        {:samples      (take samples-per-block samples)
                                         :end          (< (count samples) (inc samples-per-block))
                                         :page         samples-page

                                         :tags-samples (take samples-per-block tags-samples)
                                         :tags-end     (< (count tags-samples) (inc samples-per-block))
                                         :tags-page    tags-page})))))

(defn repo-page [request]
  (let [repo (get-repo request)
        versions (db-req/versions (get-db request) {:repo-id (:id repo)})
        versions-with-samples (filter (comp pos? :samples-count) versions)]
    (repo-view/page (merge (get-app-data request)
                           {:repo     repo
                            :versions versions-with-samples}))))

(defn version-page [request]
  (let [repo (get-repo request)
        version (get-version request)
        page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
        samples (db-req/samples-by-version (get-db request) {:version_id (:id version)
                                                             :offset     (* samples-per-page page)
                                                             :count      (inc samples-per-page)})]
    (version-view/page (merge (get-app-data request)
                              {:samples (take samples-per-page samples)
                               :end     (< (count samples) (inc samples-per-page))
                               :page    page
                               :version version
                               :repo    repo}))))

(defn signup-page [request]
  (register-view/page (get-app-data request)))

(defn signin-page [request]
  (auth-view/page (get-app-data request)))

(defn profile-page [request]
  (profile-view/page (get-app-data request)))

(defn tags-page [request]
  (tags-view/page (get-app-data request)))

(defn tag-page [request]
  (let [tag (-> request :route-params :*)
        page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
        samples (db-req/samples-by-tag (get-db request) {:count  (inc samples-per-page)
                                                         :offset (* samples-per-page page)
                                                         :tag    tag})]
    ;(prn "tag-page: " (count samples))
    (tag-view/page (merge {:samples  (take samples-per-page samples)
                           :end      (< (count samples) (inc samples-per-page))
                           :page     page
                           :tag      tag
                           :tag-data (tags-data/get-tag-data tag)}
                          (get-app-data request)))))

(defn repos-page [request]
  (repos-view/page (get-app-data request)))
;; =====================================================================================================================
;; Marketing pages
;; =====================================================================================================================
(defn chart-types-page [request]
  (let [page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
        chart-types chartopedia/chart-types
        is-end (chart-types-view/is-end (count chart-types) page)]
    (chart-types-view/page (get-app-data request) chart-types is-end page)))

(defn chart-type-page [request]
  (let [chart-name (-> request :params :chart-type)]
    (when-let [chart-type (chartopedia/get-chart chart-name)]
      (let [
            ;TODO: emliminate addin 's' to end of chart-type-name
            tag (str (:name chart-type) "s")
            page (dec (try (-> request :params :page Integer/parseInt) (catch Exception _ 1)))
            samples (db-req/samples-by-tag (get-db request) {:count  (inc samples-per-block)
                                                             :offset (* samples-per-block page)
                                                             :tag    tag})]
        (chart-type-view/page (merge {:samples (take samples-per-block samples)
                                      :end     (< (count samples) (inc samples-per-block))
                                      :page    page
                                      :tag     tag}
                                     (get-app-data request))
                              chart-type
                              (chartopedia/get-relations chart-type))))))

(defn chart-types-categories-page [request]
  (chart-type-categories-view/page (get-app-data request) chartopedia/categories))

(defn chart-types-category-page [request]
  (let [category-name (-> request :params :category)]
    (when-let [category (chartopedia/get-category category-name)]
      (chart-type-category-view/page (get-app-data request) category))))

(defn data-sets-page [request]
  (data-sets-view/page (get-app-data request)))

(defn data-set-page [request]
  (let [data-source-name (-> request :params :data-source)
        data-set-name (-> request :params :data-set)
        data-set (db-req/data-set-by-name (get-db request) {:data-source-name data-source-name
                                                            :name             data-set-name})]
    (when data-set
      (data-set-view/page (merge (get-app-data request)
                                 {:data-set data-set})))))

(defn about-page [request]
  (about-view/page (get-app-data request)))

(defn support-page [request]
  (support-view/page (get-app-data request)))

(defn roadmap-page [request]
  (roadmap-view/page (get-app-data request)))

(defn pricing-page [request]
  (pricing-view/page (get-app-data request)))

(defn pricing-enterprise-page [request]
  (pricing-enterprise-view/page (get-app-data request)))

(defn version-history-page [request]
  (version-history-view/page (get-app-data request)))

;; =====================================================================================================================
;; API
;; =====================================================================================================================
(defn update-repo [request]
  (let [repo (get-repo request)]
    (redis/enqueue (get-redis request)
                   (get-redis-queue request)
                   (:name repo))
    (response (str "Start updating: " (:name repo)))))

(defn top-landing-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        samples (db-req/top-samples (get-db request) {:count  (inc samples-per-block)
                                                      :offset offset})
        result {:samples (take samples-per-block samples)
                :end     (< (count samples) (inc samples-per-block))}]
    (response result)))

(defn top-landing-tag-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        samples (db-req/get-top-tags-samples (get-db request) {:count  (inc samples-per-block)
                                                               :offset offset})
        result {:samples (take samples-per-block samples)
                :end     (< (count samples) (inc samples-per-block))}]
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
    (response result)))

(defn top-tag-samples [request]
  (let [offset* (-> request :params :offset)
        offset (if (int? offset*) offset* (Integer/parseInt offset*))
        samples-count (-> request :params :samples-count)
        tag (-> request :params :tag)
        samples (db-req/samples-by-tag (get-db request) {:tag    tag
                                                         :count  (inc samples-count)
                                                         :offset offset})
        result {:samples (take samples-count samples)
                :end     (< (count samples) (inc samples-count))}]
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
  ;(prn "Fork: " (-> request :session :user) (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (web-utils/new-hash)
        sample* (assoc sample
                  :url hash
                  :version 0
                  :owner-id (-> request :session :user :id))]
    (let [id (db-req/add-sample! (get-db request) sample*)]
      (db-req/update-version-user-samples-latest! (get-db request) {:latest  true
                                                                    :url     hash
                                                                    :version 0})
      (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
    (response {:status   :ok
               :hash     hash
               :version  0
               :owner-id (:id (get-user request))})))

(defn save [request]
  ;(prn "Save: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (:url sample)
        db-sample (when (and hash (seq hash))
                    (db-req/sample-template-by-url (get-db request) {:url hash}))]
    (if (and db-sample
             (nil? (:template-id db-sample))
             (nil? (:version-id db-sample))
             (= (:id (get-user request)) (:owner-id db-sample)))
      (let [version (:version db-sample)
            new-version (inc version)
            sample* (assoc sample :version new-version
                                  :owner-id (-> request :session :user :id))]
        (let [id (db-req/add-sample! (get-db request) sample*)]
          (db-req/update-all-user-samples-latest! (get-db request) {:latest  false
                                                                    :url     hash
                                                                    :version new-version})
          (db-req/update-version-user-samples-latest! (get-db request) {:latest  true
                                                                        :url     hash
                                                                        :version new-version})
          (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
        (response {:status   :ok
                   :hash     hash
                   :version  new-version
                   :owner-id (:id (get-user request))}))
      (fork request))))

(defn- generate-previews [samples request]
  (let [ids (map :id samples)]
    (if (seq ids)
      (do (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) ids)
          (response (str "Start generate previews for " (count samples) " samples: "
                         (clojure.string/join ", " (map :name samples)))))
      "All samples have previews")))

(defn user-previews [request]
  (generate-previews (db-req/user-samples-without-preview (get-db request)) request))

(defn repo-previews [request]
  (generate-previews (db-req/repo-samples-without-preview (get-db request)) request))

(defn signup [request]
  (let [username (-> request :params :username)
        fullname (-> request :params :fullname)
        email (-> request :params :email)
        password (-> request :params :password)]
    (prn "signup" username fullname email password)
    (if (and (seq username) (seq fullname) (seq email) (seq password))
      (let [salt (web-utils/new-salt)
            hash (bcrypt/encrypt (str password salt))
            db-user {:fullname    fullname
                     :username    username
                     :email       email
                     :password    hash
                     :salt        salt
                     :permissions auth-base/base-perms}
            id (db-req/add-user<! (get-db request) db-user)
            user (assoc db-user :id id)]
        (timbre/info "signup" (str password salt) hash)
        (assoc-in (redirect "/") [:session :user] user))
      "Bad values")))

(defn signin [request]
  (let [username (-> request :params :username)
        password (-> request :params :password)]
    (prn "signin" username password)
    (if (and (seq username) (seq password))
      (if-let [user (db-req/get-user-by-username-or-email (get-db request) {:username username})]
        (if (bcrypt/check (str password (:salt user)) (:password user))
          (do
            (prn "auth: " user)
            (assoc-in (redirect "/") [:session :user] user))))
      "Bad values")))

(defn signout [request]
  (assoc-in (redirect "/") [:session :user]
            (auth/create-anonymous-user (get-db request))))

(defn redirect-slash [request]
  (redirect (web-utils/drop-slash (:uri request)) 301))

;; =====================================================================================================================
;; Routes
;; =====================================================================================================================
(defroutes app-routes
           (route/resources "/")

           (GET "/" [] (-> landing-page
                           mw/base-page-middleware))

           (GET "/generate-preview/:id" [] (fn [request]
                                             (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue)
                                                            [(-> request :route-params :id Integer/parseInt)])))

           ;; Marketing pages
           (GET "/chart-types" [] (-> chart-types-page
                                      mw/base-page-middleware))

           (GET "/chart-types/:chart-type" [] (-> chart-type-page
                                                  mw/base-page-middleware))

           (GET "/chart-types/categories" [] (-> chart-types-categories-page
                                                 mw/base-page-middleware))

           (GET "/chart-types/categories/:category" [] (-> chart-types-category-page
                                                           mw/base-page-middleware))

           (GET "/datasets/" [] redirect-slash)
           (GET "/datasets" [] (-> data-sets-page
                                   mw/all-data-sets-middleware
                                   mw/base-page-middleware))

           (GET "/datasets/:data-source/:data-set" [] (-> data-set-page
                                                          mw/base-page-middleware))

           (GET "/support" [] (-> support-page
                                  mw/base-page-middleware))

           (GET "/roadmap" [] (-> roadmap-page
                                  mw/base-page-middleware))

           (GET "/version-history" [] (-> version-history-page
                                          mw/base-page-middleware))

           (GET "/pricing" [] (-> pricing-page
                                  mw/base-page-middleware))

           (GET "/pricing/enterprise" [] (-> pricing-enterprise-page
                                             mw/base-page-middleware))

           (GET "/about" [] (-> about-page
                                mw/base-page-middleware))

           ;; End marketing pages
           (GET "/tags/" [] redirect-slash)
           (GET "/tags" [] (-> tags-page
                               mw/all-tags-middleware
                               mw/base-page-middleware))

           (GET "/tags/*" [] (-> tag-page
                                 mw/base-page-middleware))

           (GET "/profile" [] (-> profile-page
                                  mw/base-page-middleware))

           (GET "/signin" [] (-> signin-page
                                 (mw/base-page-middleware :signin)))

           (GET "/signup" [] (-> signup-page
                                 (mw/base-page-middleware :signup)))

           (POST "/signin" [] (-> signin
                                  (auth/permissions-middleware :signin)
                                  auth/check-anonymous-middleware))

           (POST "/signup" [] (-> signup
                                  (auth/permissions-middleware :signup)
                                  auth/check-anonymous-middleware))

           (GET "/signout" [] (-> signout
                                  ;(auth/permissions-middleware :signout)
                                  auth/check-anonymous-middleware))

           (GET "/new" [] new)
           (POST "/run" [] run)
           (POST "/save" [] save)
           (POST "/fork" [] fork)

           (GET "/_user_previews_" [] user-previews)
           (GET "/_repo_previews_" [] repo-previews)

           (GET "/:repo/_update_" [] (mw/check-repo-middleware
                                       update-repo))
           (POST "/:repo/_update_" [] (mw/check-repo-middleware
                                        update-repo))

           (GET "/landing-samples.json" [] top-landing-samples)
           (POST "/landing-samples.json" [] top-landing-samples)
           (GET "/landing-tag-samples.json" [] top-landing-tag-samples)
           (POST "/landing-tag-samples.json" [] top-landing-tag-samples)
           (GET "/version-samples.json" [] top-version-samples)
           (POST "/version-samples.json" [] top-version-samples)
           (GET "/tag-samples.json" [] top-tag-samples)
           (POST "/tag-samples.json" [] top-tag-samples)


           (GET "/projects/" [] redirect-slash)
           (GET "/projects" [] (-> repos-page
                                   mw/base-page-middleware))

           (GET "/projects/:repo" [] (-> repo-page
                                         mw/check-repo-middleware
                                         mw/base-page-middleware))
           (GET "/projects/:repo/" [] (fn [request]
                                        (when ((-> repo-page
                                                   mw/check-repo-middleware
                                                   mw/base-page-middleware) request)
                                          (redirect-slash request))))

           (GET "/projects/:repo/:version" [] (-> version-page
                                                  mw/check-version-middleware
                                                  mw/check-repo-middleware
                                                  mw/base-page-middleware))
           (GET "/projects/:repo/:version/" [] (fn [request]
                                                 (when ((-> version-page
                                                            mw/check-version-middleware
                                                            mw/check-repo-middleware
                                                            mw/base-page-middleware) request)
                                                   (redirect-slash request))))

           (GET "/:repo/:version/*" [] (-> show-sample
                                           mw/check-sample-middleware
                                           mw/check-version-middleware
                                           mw/check-repo-middleware
                                           auth/check-anonymous-middleware))
           ;; for canonical links
           (GET "/:repo/*" [] (-> show-sample
                                  mw/check-sample-middleware
                                  mw/check-version-middleware
                                  mw/check-repo-middleware
                                  auth/check-anonymous-middleware))

           (GET "/:hash/" [] (-> show-last-user-sample
                                 auth/check-anonymous-middleware))
           (GET "/:hash" [] (-> show-last-user-sample
                                auth/check-anonymous-middleware))
           (GET "/:hash/:version" [] (-> show-user-sample
                                         auth/check-anonymous-middleware))
           (GET "/:hash/:version/" [] (-> show-user-sample
                                          auth/check-anonymous-middleware))
           (route/not-found "404 Page not found"))