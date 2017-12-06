(ns playground.views.marketing.about-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "About | AnyChart Playground"
                :description "AnyChart Playground is an online tool for testing and showcasing user-created HTML, CSS and JavaScript code snippets."})
    [:body page/body-tag-manager
     [:div.wrapper.about-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:h1.page-caption "About"]
        [:p "AnyChart Playground is an online tool for testing and showcasing user-created HTML, CSS and JavaScript code snippets."]

        [:p "This playground is used by "
         [:a {:title "AnyChart" :href "https://www.anychart.com/"} "AnyChart Team"]
         " to store and showcase samples from "
         [:a {:title "AnyChart Documentation" :href "https://docs.anychart.com/"} "AnyChart Documentation"]
         ", "
         [:a {:title "AnyChart API Reference" :href "https://api.anychart.com/"} "AnyChart API Reference"]
         ", and "
         [:a {:title "AnyChart Chartopedia" :href "https://www.anychart.com/chartopedia/"} "AnyChart Chartopedia"]
         "."]

        [:p
         "Feel free to use AnyChart Playground if you are a customer of AnyChart, evaluating "
         [:a {:title "GraphicsJS" :href "https://www.anychart.com/"} "AnyChart HTML5 Charts"]
         ", using "
         [:a {:title "GraphicsJS" :href "http://www.graphicsjs.org/"} "GraphicsJS"]
         " library or you just want to create and share any other HTML, CSS and JavaScript code snippet."]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))