(ns playground.views.landing-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.landing-page-utils :as landing]
            [playground.views.prev-next-buttons :as prev-next-buttons]
            [playground.data.tags :as tags-data]))


(defn page [{:keys [page tags-page all-tags] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (landing/title page)
                :description "The ultimate charts playground. Create, modify, browse, learn and share."})
    [:body
     page/body-tag-manager
     [:div.wrapper.landing-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1 "AnyChart "
             [:b "Playground"]]
            [:p.description "The ultimate charts playground. Create, modify, browse, learn and share."]]]]]]]

      [:div.create-box
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          (page/create-box (:templates data))]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:p.popular-label "Popular " [:b "samples"]]
        [:div#popular-samples.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]

        (prev-next-buttons/buttons "popular-samples-prev"
                                   "popular-samples-next"
                                   page
                                   (:end data)
                                   "/?page=")

        [:p.popular-label.popular-tags-label "Popular " [:b "tags"]]

        [:div.popular-tags-box
         (for [tag (take 60 all-tags)]
           [:a.popular-tag-button
            {:title (str "Tag - " (:name tag))
             :href  (str "/tags/" (tags-data/original-name->id-name (:name tag)))}
            (:name tag)])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script (page/run-js-fn "playground.site.pages.landing_page.startLanding" (:end data) page)]]))