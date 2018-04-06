(ns playground.views.marketing.roadmap-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Roadmap | AnyChart Playground"
                :description "AnyChart Playground is one of the favourite projects of AnyChart team. This is a tool we use daily and we have big plans to make Playground even better. The list below is an approximate Playground roadmap and it is subject to change."})
    [:body page/body-tag-manager
     [:div.wrapper.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Roadmap"]
        [:p "AnyChart Playground is one of the favourite projects of AnyChart team. This is a tool we use daily and we have big plans to make Playground even better. The list below is an approximate Playground roadmap and it is subject to change."]

        [:div.line-container

         [:div
          [:h2 "Version 1.1.0" [:span.muted "Search"]]
          [:div
           [:ul
            [:li "Ability to search samples by names, descriptions, and tags."]]]]

         [:div
          [:h2 "Version 1.2.0" [:span.muted "Documentation and API Reference"]]
          [:div
           [:ul
            [:li "Deep API and Documentation integration."]]]]


         [:div
          [:h2 "Version 1.3.0" [:span.muted "Download and Share"]]
          [:div
           [:ul
            [:li "Ability to download samples as an HTML file, PDF file or Image"]
            [:li "Ability to share samples with Facebook, LinkedIn, Pinterest, Twitter and Instagram."]]]]

         [:div
          [:h2 "Version 2.0.0" [:span.muted "Personalization"]]
          [:div
           [:ul
            [:li "User accounts."]
            [:li "Privacy Settings."]
            [:li "Samples collections."]
            [:li "Assets Hosting."]
            [:li "Ability to like samples."]
            ]]]
         ]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     ]))