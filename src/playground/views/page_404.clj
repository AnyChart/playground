(ns playground.views.page-404
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "404 error | AnyChart Playground"
                :description "Page not found"})
    [:body page/body-tag-manager
     [:div.wrapper.page-404

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1
             [:b "Error 404"]]
            [:p.description "This page you were trying to reach at this address doesn't seem to exist.<br>
            This is usually the result of a bad or outdated link.<br>
            We apologize for any inconvenience."]]]]]]]

      [:div.content
       [:div.container-fluid.content-container

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))