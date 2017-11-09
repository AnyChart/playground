(ns playground.web.handlers.marketing-handlers
  (:require
    ;; web
    [playground.web.helpers :refer :all]
    ;; views
    [playground.views.marketing.about-page :as about-view]
    [playground.views.marketing.support-page :as support-view]
    [playground.views.marketing.roadmap-page :as roadmap-view]
    [playground.views.marketing.pricing-page :as pricing-view]
    [playground.views.marketing.pricing-enterprise-page :as pricing-enterprise-view]
    [playground.views.marketing.version-history-page :as version-history-view]))

;; =====================================================================================================================
;; Marketing static pages
;; =====================================================================================================================

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
