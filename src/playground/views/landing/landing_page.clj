(ns playground.views.landing.landing-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.landing-page-utils :as landing]
            [playground.views.prev-next-buttons :as prev-next-buttons]
            [playground.data.tags :as tags-data]))


(defn pagination [page max-page end class]
  (prev-next-buttons/pagination "popular-samples-prev"
                                "popular-samples-next"
                                page
                                max-page
                                end
                                "/?page="
                                class))


(defn page [{{samples  :samples
              total    :total
              max-page :max-page
              end      :end} :result
             page            :page
             all-tags        :all-tags
             :as             data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (landing/title page)
                :description (str "The ultimate charts playground. Create, modify, browse, learn and share. "
                                  "AnyChart Playground is an online tool for testing and showcasing user-created HTML, "
                                  "CSS and JavaScript code snippets. This playground is used by AnyChart Team to store "
                                  "and showcase samples from AnyChart Documentation, AnyChart API Reference, and AnyChart Chartopedia.")})
    [:body
     page/body-tag-manager

     [:div.wrapper.landing-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div.text
           [:h1 "AnyChart "
            [:b "Playground"]]
           [:p.description "The ultimate charts playground. Create, modify, browse, learn and share."]]]]]]

      [:div.create-box.d-lg-block
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          (page/create-box (:templates data))]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:p.popular-label "Popular " [:b "samples"]]
        (pagination page max-page end "top")

        [:div#popular-samples.samples-container.row.justify-content-between
         (sample-view/samples samples)]

        (pagination page max-page end "bottom")

        [:p.popular-label.popular-tags-label "Popular " [:b "tags"]]

        [:div.popular-tags-box
         (for [tag (take 60 all-tags)]
           [:a.popular-tag-button
            {:title (str "Tag - " (:name tag))
             :href  (str "/tags/" (tags-data/original-name->id-name (:name tag)))}
            (:name tag)])]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     page/jquery-script
     page/bootstrap-script
     page/site-script
     [:script (page/run-js-fn "playground.site.pages.landing_page.startLanding" page max-page end)]]))