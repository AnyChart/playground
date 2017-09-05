(ns playground.views.tag-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]))

(defn page [{:keys [page tag] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:p.popular-label "Tag " [:b "samples"]]
        [:div#tag-samples.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]
        [:div.prev-next-buttons
         [:a#tag-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                                           :href  (str "/tags/" tag "?page=" page)
                                                           :title (str "Prev page, " page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#tag-samples-next.next-button.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))
                                                           :href  (str "/tags/" tag "?page=" (-> page inc inc))
                                                           :title (str "Next page, " (-> page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.startTagPage(" (:end data) ", " page ", '" tag "');"]]))