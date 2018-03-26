(ns playground.views.tag.tags-stat-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [playground.data.tags :as tags-data]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Tags Statistics | AnyChart Playground"
                :description "Tags Statisitics"})
    [:body page/body-tag-manager
     [:div.wrapper.tags-stat-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:p.popular-label [:b "tags"] " by usage"]

        [:div.popular-tags-box
         (for [tag (:all-tags data)]
           [:div.line-tag
            [:div.tag-container
             [:a.popular-tag-button
              {:title (str "Tag - " (:name tag))
               :href  (str "/tags/" (tags-data/original-name->id-name (:name tag)))}
              (:name tag)
              ]]
            [:span.count (:count tag)]])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]]))