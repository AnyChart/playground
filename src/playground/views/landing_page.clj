(ns playground.views.landing-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [{:keys [page tags-page all-tags] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body
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
            [:p.description "is a place where all your data visualization dreams come true"]]]]]]]

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

        [:div.prev-next-buttons
         [:a#popular-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                                               :href  (str "/?samples=" page)
                                                               :title (str "Prev page, " page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#popular-samples-next.next-button.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))
                                                               :href  (str "/?samples=" (-> page inc inc))
                                                               :title (str "Next page, " (-> page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]



        [:p.popular-label.popular-tags-label "Popular " [:b "tags"]]

        [:div.popular-tags-box
         (for [tag (take 60 all-tags)]
           [:a.popular-tag-button
            {:title (str "Tag - " (:name tag))
             :href  (str "/tags/" (:name tag))}
            (:name tag)])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.startLanding(" (:end data) ", " page ");"
      ;"playground.site.landing.startLandingTag(" (:tags-end data) ", " tags-page ");"
      ]
     ]))