(ns playground.views.landing-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [{:keys [page tags-page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          (page/jumbotron (:templates data))]]]]

      [:div.create-box
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          (page/create-box (:templates data))]]]
       ]

      [:div.content
       [:div.container-fluid.content-container.landing-page

        [:p.popular-label "Popular " [:b "samples"]]
        [:div#popular-samples.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]

        [:div.prev-next-buttons
         [:a#popular-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                                               :href  (str "/?page=" page)
                                                               :title (str "Prev page, " page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#popular-samples-next.next-button.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))
                                                               :href  (str "/?page=" (-> page inc inc))
                                                               :title (str "Next page, " (-> page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]



        [:p.popular-label "Popular " [:b "tags"]]
        [:div#popular-tags.row.samples-container
         (for [sample (:tags-samples data)]
           (sample-view/sample-landing sample))]
        [:div.prev-next-buttons
         [:a#popular-tags-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? tags-page) "none;" "inline-block;"))
                                                            :href  (str "/?page=" tags-page)
                                                            :title (str "Prev page, " tags-page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#popular-tags-next.next-button.btn.btn-default {:style (str "display: " (if (:tags-end data) "none;" "inline-block;"))
                                                            :href  (str "/?page=" (-> tags-page inc inc))
                                                            :title (str "Next page, " (-> tags-page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.startLanding(" (:end data) ", " page ");
               playground.site.landing.startLandingTag(" (:tags-end data) ", " tags-page ");"]
     ]))