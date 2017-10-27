(ns playground.views.marketing.roadmap-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Roadmap | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body
     [:div.wrapper.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Roadmap"]
        [:p "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Architecto aut culpa deserunt id numquam sed temporibus voluptatem! Animi delectus dolore eos excepturi optio, quasi quibusdam reiciendis suscipit tenetur ullam velit."]

        [:div.line-container
         [:div
          [:h2 "Version 1.1.0" [:span.muted "Like, Download and Share"]]
          [:div
           [:ul
            [:li "Ability to download samples as HTML file, PDF file or Vector/Raster image"]
            [:li "Ability to share samples with Facebook, LinkedIn, Pinterest, Twitter and Instagram"]
            [:li "Ability to like samples"]]]]

         [:div
          [:h2 "Version 1.2.0" [:span.muted "Search for Samples"]]
          [:div
           [:ul
            [:li "Ability to search samples by names, description, tags and used data sets"]]]]

         [:div
          [:h2 "Version 1.3.0" [:span.muted "Documentation and API Reference"]]
          [:div
           [:ul
            [:li "Плейграунд будет по коду примера саджестить статьи, ссылки на API Reference и чартопедию на странице примера и в редакторе."]]]]

         [:div
          [:h2 "Version 2.0.0" [:span.muted "Personalization"]]
          [:div
           [:ul
            [:li "Личный кабинет"]
            [:li "Privacy Settings"]
            [:li "Коллекции примеров"]
            [:li "Assets Hosting"]
            ]]]
         ]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))