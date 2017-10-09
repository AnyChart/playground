(ns playground.views.marketing.roadmap-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Roadmap"]

        [:div.line-container
         [:div
          [:h2 "Version 1.1.0" [:span.muted "Likes, Download and Share"]]
          [:div
           [:ul
            [:li "Ability to download samples as HTML file, PDF file or Vector/Raster image"]
            [:li "Ability to share samples with Facebook, LinkedIn, Pinterest, Twitter and Instagram"]
            [:li "Ability to like samples"]]]]

         [:div
          [:h2 "Version 1.2.0" [:span.muted "Search Everywhare"]]
          [:div
           [:ul
            [:li "Ability to search samples by names, tags and used data sets"]]]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))