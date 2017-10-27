(ns playground.views.tag.tags-stat-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Tags Statistics | AnyChart Playground"
                :description "Tags Statisitics"})
    [:body
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
               :href  (str "/tags/" (:name tag))}
              (:name tag)
              ]]
            [:span.count (:count tag)]])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))