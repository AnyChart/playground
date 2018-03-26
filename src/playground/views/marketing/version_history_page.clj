(ns playground.views.marketing.version-history-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Version History | AnyChart Playground"
                :description "AnyChart Playground evolves along with AnyChart Charts, check out what went through on this page and see what we are planning."})
    [:body page/body-tag-manager
     [:div.wrapper.version-history-page.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Version History"]
        [:p
         "AnyChart Playground evolves along with "
         [:a {:title "AnyChart Charts" :href "https://www.anychart.com/"} "AnyChart Charts"]
         ", check out what went through on this page and see "
         [:a {:title "Playground Roadmap" :href "/roadmap"} "what we are planning"]
         "."]

        [:div.line-container
         [:div
          ;region ---- version 1.0.0
          [:h2 "Version 1.0.0" [:span.muted "Dec 2017"]]
          [:p "Rebirth of AnyChart Playground as an online community for testing and showcasing user-created HTML, CSS and JavaScript code snippets."]]

         [:div.row
          [:div.col-md-4
           [:p [:span.glyphicon.glyphicon-ok] "Fork Samples"]
           [:ul
            [:li "Any sample can now be forked and saved."]]]
          [:div.col-md-4
           [:p [:span.glyphicon.glyphicon-ok] "\"View Only\" mode"]
           [:ul
            [:li "\"View only\" mode to show the description, files used, and tags."]]]
          [:div.col-md-4
           [:p [:span.glyphicon.glyphicon-ok] "Brand New Editor"]
           [:ul
            [:li "Ability to modify JavaScript, HTML and CSS code."]]]
          [:div.col-md-4
           [:p [:span.glyphicon.glyphicon-ok] "Embedding"]
           [:ul
            [:li "Ability to embed charts from Playground and use on your website."]]]]]
        ]]
      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]]))