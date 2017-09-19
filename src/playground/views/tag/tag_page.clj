(ns playground.views.tag.tag-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]))

(defn page [{:keys [page tag] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper.tag-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1 [:b tag]]

        (when-not :description
          [:p.tag-description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nec scelerisque urna, eget lobortis felis. Nullam vel est semper, pellentesque dolor fringilla, imperdiet sem. Morbi accumsan vel neque vel scelerisque. Sed suscipit dictum erat eget hendrerit. In magna nunc, faucibus eu fringilla eu, dapibus vel ex. Aliquam mollis sollicitudin varius. In vulputate pretium nulla. Phasellus odio elit, interdum quis vestibulum eu, malesuada in massa. Vivamus pretium pulvinar arcu, a scelerisque nunc. Praesent a magna vitae est suscipit convallis placerat eu metus."]
          )
        (when-not :description
          [:h2 "Samples"]
          )

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
     [:script "playground.site.landing.startTagPage(" (:end data) ", " page ", '" tag "', true);"]]))