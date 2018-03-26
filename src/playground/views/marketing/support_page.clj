(ns playground.views.marketing.support-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Support | AnyChart Playground"
                :description "Welcome to AnyChart support center. Our priority is a customer satisfaction. We constantly improve the quality of our products and add new features. Our team is very open to any suggestions regarding our products. We listen what you want."})
    [:body page/body-tag-manager
     [:div.wrapper.support-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1
             [:b "AnyChart Support"]]
            [:p.description "Welcome to AnyChart support center. Our priority is a customer satisfaction. We constantly improve the quality of our products and add new features. Our team is very open to any suggestions regarding our products. We listen what you want."]]]]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:h1 "Technical Support"]
        [:div.row
         [:div.col-md-5.text "If you have any problems with AnyChart software, please, be sure that you have installed the latest update before contacting our technical support. It happens that users find bugs that have been already fixed in the latest builds of products."]
         [:div.col-md-7
          [:div.big-text.block-24 "We provide guaranteed answers during 24 hours on working days \nand 48 hours on week-ends."]]]

        [:h1 "Contact our Technical Support Team"]
        [:div.row
         [:div.col-md-5.text
          [:div.big-link.support [:a {:href "http://support.anychart.com/"} "Online support system"]]
          [:div.big-link.email [:a {:href "mailto:support@anychart.com"} "support@anychart.com"]]]
         [:div.col-md-7
          [:div.big-text.block-info
           [:span.yellow-label "Please always include the following information:"]
           [:ul
            [:li "The description of problem."]
            [:li "Link to a sample in playground."]]]]]

        [:h1 "Ask on Stack Overflow"]
        [:div.text
         [:span "You can also ask any question on Stack Overflow with "]
         [:a.tag {:href "//stackoverflow.com/tags/anychart"} "Anychart"]
         [:span " tag and get a chance to get some feedback from the community or AnyChart support engineers or developers who are also monitoring questions there and may have an insight."]

         [:div.big-links
          [:div.big-link.search [:a {:href "//stackoverflow.com/tags/anychart"} "Search for similar question"]]
          [:div.big-link.post [:a {:href "//stackoverflow.com/questions/ask?tags=anychart"} "Post your own question"]]]

         [:p.text "Note that there is no guaranteed feedback time on Stack Overflow and you should open a ticket in "
          [:a {:href "http://support.anychart.com/"} "official support system"]
          " to report bugs and make feature requests, especially if you own an active license subscription."]]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]]))