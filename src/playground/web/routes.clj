(ns playground.web.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect file-response content-type]]
            [taoensso.timbre :as timbre]
            [selmer.parser :refer [render-file]]
    ;; components
            [playground.db.request :as db-req]
            [playground.redis.core :as redis]
            [playground.utils.utils :as common-utils]
            [playground.preview-generator.phantom :as phantom]
    ;; web
            [playground.web.utils :as web-utils :refer [response]]
            [playground.web.auth :as auth]
            [playground.web.middleware :as mw]
            [playground.web.auth-base :as auth-base]
            [playground.web.helpers :refer :all]
    ;; pages
            [playground.views.page-404 :as view-404]
            [playground.utils.utils :as utils]

    ;; web handlers
            [playground.web.handlers.landing-handlers :as landing-handlers]
            [playground.web.handlers.sample.pages :as sample-handlers]
            [playground.web.handlers.sample.api :as sample-api]
            [playground.web.handlers.repo-handlers :as repo-handlers]
            [playground.web.handlers.tag-handlers :as tag-handlers]
            [playground.web.handlers.chart-type-handlers :as chart-type-handlers]
            [playground.web.handlers.dataset-handlers :as dataset-handlers]
            [playground.web.handlers.marketing-handlers :as marketing-handlers]
            [playground.web.handlers.session-handlers :as session-handlers]
            [playground.web.handlers.sitemap-handler :as sitemap-handler]
            [playground.web.handlers.generator-handlers :as generator-handlers]
            ))

;; =====================================================================================================================
;; Route utils
;; =====================================================================================================================

(defn redirect-slash [request]
  (redirect (web-utils/drop-slash (:uri request)) 301))

(defn page-404 [request]
  (view-404/page (get-app-data request)))

;; =====================================================================================================================
;; Routes
;; =====================================================================================================================
(defroutes app-routes
           (route/resources "/")

           (GET "/*/" [] redirect-slash)

           (GET "/" [] (-> landing-handlers/landing-page
                           mw/pagination-page-middleware
                           mw/all-tags-middleware
                           mw/base-page-middleware))

           (GET "/sitemap.xml" [] (-> sitemap-handler/sitemap-page
                                      mw/repos-middleware
                                      mw/all-tags-middleware
                                      mw/all-data-sets-middleware))

           (GET "/generate-preview/:id" [] (fn [request]
                                             (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue)
                                                            [(-> request :route-params :id Integer/parseInt)])))

           ;; ==========================================================================================================
           ;; Chart types routes
           ;; ==========================================================================================================
           (GET "/chart-types" [] (-> chart-type-handlers/chart-types-page
                                      mw/base-page-middleware))

           (GET "/chart-types/:chart-type" [] (-> chart-type-handlers/chart-type-page
                                                  mw/pagination-page-middleware
                                                  mw/base-page-middleware))

           (GET "/chart-types/categories" [] (-> chart-type-handlers/chart-types-categories-page
                                                 mw/base-page-middleware))

           (GET "/chart-types/categories/:category" [] (-> chart-type-handlers/chart-types-category-page
                                                           mw/base-page-middleware))
           ;; ==========================================================================================================
           ;; Dataset routes
           ;; ==========================================================================================================
           ; (GET "/datasets/" [] redirect-slash)
           (GET "/datasets" [] (-> dataset-handlers/data-sets-page
                                   mw/pagination-page-middleware
                                   mw/all-data-sets-middleware
                                   mw/base-page-middleware))

           (GET "/datasets/:data-source/:data-set" [] (-> dataset-handlers/data-set-page
                                                          mw/base-page-middleware))

           (GET "/datasets/:data-set" [] (-> dataset-handlers/data-set-page
                                             mw/base-page-middleware))

           ;; ==========================================================================================================
           ;; Marketing statis pages routes
           ;; ==========================================================================================================

           (GET "/support" [] (-> marketing-handlers/support-page
                                  mw/base-page-middleware))

           (GET "/roadmap" [] (-> marketing-handlers/roadmap-page
                                  mw/base-page-middleware))

           (GET "/version-history" [] (-> marketing-handlers/version-history-page
                                          mw/base-page-middleware))

           (GET "/pricing" [] (-> marketing-handlers/pricing-page
                                  mw/base-page-middleware))

           (GET "/pricing/enterprise" [] (-> marketing-handlers/pricing-enterprise-page
                                             mw/base-page-middleware))

           (GET "/about" [] (-> marketing-handlers/about-page
                                mw/base-page-middleware))
           ;; ==========================================================================================================
           ;; Tags routes
           ;; ==========================================================================================================
           ;(GET "/tags/" [] redirect-slash)
           (GET "/tags" [] (-> tag-handlers/tags-page
                               mw/all-tags-middleware
                               mw/base-page-middleware))

           (GET "/tags/index" [] (-> tag-handlers/tag-stat-page
                                     mw/all-tags-middleware
                                     mw/base-page-middleware))

           (GET "/tags/*" [] (-> tag-handlers/tag-page
                                 mw/pagination-page-middleware
                                 mw/base-page-middleware))

           ;; ==========================================================================================================
           ;; Session routes
           ;; ==========================================================================================================
           (GET "/profile" [] (-> session-handlers/profile-page
                                  mw/base-page-middleware))

           (GET "/signin" [] (-> session-handlers/signin-page
                                 (mw/base-page-middleware :signin)))

           (GET "/signup" [] (-> session-handlers/signup-page
                                 (mw/base-page-middleware :signup)))

           (POST "/signin" [] (-> session-handlers/signin
                                  (auth/permissions-middleware :signin)
                                  auth/check-anonymous-middleware))

           (POST "/signup" [] (-> session-handlers/signup
                                  (auth/permissions-middleware :signup)
                                  auth/check-anonymous-middleware))

           (GET "/signout" [] (-> session-handlers/signout
                                  ;(auth/permissions-middleware :signout)
                                  auth/check-anonymous-middleware))

           ;; ==========================================================================================================
           ;; Samples routes
           ;; ==========================================================================================================

           (GET "/new" [] (-> sample-handlers/show-sample-editor mw/check-template-middleware))
           (GET "/new/editor" [] (-> sample-handlers/show-sample-editor mw/check-template-middleware))
           (GET "/new/view" [] (-> sample-handlers/show-sample-standalone mw/check-template-middleware))
           (GET "/new/iframe" [] (-> sample-handlers/show-sample-iframe mw/check-template-middleware))
           (GET "/new/preview" [] (-> sample-handlers/show-sample-preview mw/check-template-middleware))
           (GET "/new/download" [] (-> sample-handlers/show-sample-download mw/check-template-middleware))


           (POST "/run" [] sample-api/run)
           (POST "/save" [] sample-api/save)
           (POST "/fork" [] sample-api/fork)

           ;; ==========================================================================================================
           ;; Generator routes
           ;; ==========================================================================================================
           (GET "/_user_previews_" [] generator-handlers/user-previews)
           (GET "/_repo_previews_" [] generator-handlers/repo-previews)
           (GET "/_refresh_views_" [] generator-handlers/refresh-views)

           (GET "/:repo/_update_" [] (-> generator-handlers/update-repo
                                         mw/check-repo-middleware))
           (POST "/:repo/_update_" [] (-> generator-handlers/update-repo
                                          mw/check-repo-middleware))

           (GET "/landing-samples.json" [] landing-handlers/top-landing-samples)
           (POST "/landing-samples.json" [] landing-handlers/top-landing-samples)
           ;(GET "/landing-tag-samples.json" [] top-landing-tag-samples)
           ;(POST "/landing-tag-samples.json" [] top-landing-tag-samples)
           (GET "/version-samples.json" [] repo-handlers/top-version-samples)
           (POST "/version-samples.json" [] repo-handlers/top-version-samples)
           (GET "/tag-samples.json" [] tag-handlers/top-tag-samples)
           (POST "/tag-samples.json" [] tag-handlers/top-tag-samples)


           ;(GET "/projects/" [] redirect-slash)
           (GET "/projects" [] (-> repo-handlers/repos-page
                                   mw/base-page-middleware))

           (GET "/projects/:repo" [] (-> repo-handlers/repo-page
                                         mw/check-repo-middleware
                                         mw/base-page-middleware))
           ;(GET "/projects/:repo/" [] (fn [request]
           ;                             (when ((-> repo-handlers/repo-page
           ;                                        mw/check-repo-middleware
           ;                                        mw/base-page-middleware) request)
           ;                               (redirect-slash request))))

           (GET "/projects/:repo/:version" [] (-> repo-handlers/version-page
                                                  mw/pagination-page-middleware
                                                  mw/check-version-middleware
                                                  mw/check-repo-middleware
                                                  mw/base-page-middleware))
           ;(GET "/projects/:repo/:version/" [] (fn [request]
           ;                                      (when ((-> repo-handlers/version-page
           ;                                                 mw/pagination-page-middleware
           ;                                                 mw/check-version-middleware
           ;                                                 mw/check-repo-middleware
           ;                                                 mw/base-page-middleware) request)
           ;                                        (redirect-slash request))))

           ;; projects samples
           (GET "/:repo/:version/*" [] (-> sample-handlers/show-sample-editor mw/repo-sample))
           (GET "/:repo/:version/*/editor" [] (-> sample-handlers/show-sample-editor mw/repo-sample))
           (GET "/:repo/:version/*/view" [] (-> sample-handlers/show-sample-standalone mw/repo-sample))
           (GET "/:repo/:version/*/iframe" [] (-> sample-handlers/show-sample-iframe mw/repo-sample))
           (GET "/:repo/:version/*/preview" [] (-> sample-handlers/show-sample-preview mw/repo-sample))
           (GET "/:repo/:version/*/download" [] (-> sample-handlers/show-sample-download mw/repo-sample))
           (GET "/:repo/:version/*/data" [] (-> sample-handlers/sample-data mw/repo-sample))
           (POST "/:repo/:version/*/data" [] (-> sample-handlers/sample-data mw/repo-sample))

           ;; canonical projects samples
           (GET "/:repo/*" [] (-> sample-handlers/show-sample-editor mw/repo-sample))
           (GET "/:repo/*/editor" [] (-> sample-handlers/show-sample-editor mw/repo-sample))
           (GET "/:repo/*/view" [] (-> sample-handlers/show-sample-standalone mw/repo-sample))
           (GET "/:repo/*/iframe" [] (-> sample-handlers/show-sample-iframe mw/repo-sample))
           (GET "/:repo/*/preview" [] (-> sample-handlers/show-sample-preview mw/repo-sample))
           (GET "/:repo/*/download" [] (-> sample-handlers/show-sample-download mw/repo-sample))
           (GET "/:repo/*/data" [] (-> sample-handlers/sample-data mw/repo-sample))
           (POST "/:repo/*/data" [] (-> sample-handlers/sample-data mw/repo-sample))

           ;; canonical (last) user samples
           (GET "/:hash" [] (-> sample-handlers/show-sample-editor mw/last-user-sample))
           (GET "/:hash/editor" [] (-> sample-handlers/show-sample-editor mw/last-user-sample))
           (GET "/:hash/view" [] (-> sample-handlers/show-sample-standalone mw/last-user-sample))
           (GET "/:hash/iframe" [] (-> sample-handlers/show-sample-iframe mw/last-user-sample))
           (GET "/:hash/preview" [] (-> sample-handlers/show-sample-preview mw/last-user-sample))
           (GET "/:hash/download" [] (-> sample-handlers/show-sample-download mw/last-user-sample))
           (GET "/:hash/data" [] (-> sample-handlers/sample-data mw/last-user-sample))
           (POST "/:hash/data" [] (-> sample-handlers/sample-data mw/last-user-sample))

           ;; user samples with version
           (GET "/:hash/:version" [] (-> sample-handlers/show-sample-editor mw/user-sample))
           (GET "/:hash/:version/editor" [] (-> sample-handlers/show-sample-editor mw/user-sample))
           (GET "/:hash/:version/view" [] (-> sample-handlers/show-sample-standalone mw/user-sample))
           (GET "/:hash/:version/iframe" [] (-> sample-handlers/show-sample-iframe mw/user-sample))
           (GET "/:hash/:version/preview" [] (-> sample-handlers/show-sample-preview mw/user-sample))
           (GET "/:hash/:version/download" [] (-> sample-handlers/show-sample-download mw/user-sample))
           (GET "/:hash/:version/data" [] (-> sample-handlers/sample-data mw/user-sample))
           (POST "/:hash/:version/data" [] (-> sample-handlers/sample-data mw/user-sample))

           ;; TODO: redirects for group, delete in 6-9 months
           (GET "/:repo/:version/:group/" [] (-> sample-handlers/group-redirect
                                                 mw/check-version-middleware
                                                 mw/check-repo-middleware
                                                 auth/check-anonymous-middleware))
           (GET "/:repo/:version/:group" [] (-> sample-handlers/group-redirect
                                                mw/check-version-middleware
                                                mw/check-repo-middleware
                                                auth/check-anonymous-middleware))

           (route/not-found (-> page-404
                                mw/base-page-middleware)))