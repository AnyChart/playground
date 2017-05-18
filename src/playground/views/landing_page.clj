(ns playground.views.landing-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid
        (page/jumbotron (:templates data))

        [:div#samples-container.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]
        [:div#prev-next-buttons.row.text-center
         [:a#prevButton.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                         :href  (str "/?page=" page)
                                         :title (str "Prev page, " page)} "Prev"]
         [:a#nextButton.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))
                                         :href  (str "/?page=" (-> page inc inc))
                                         :title (str "Next page, " (-> page inc inc))} "Next"]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.start(" (:end data) ", " page ");"]]))