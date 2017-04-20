(ns playground.views.version-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data))

      [:div.content
       [:div.container-fluid

        [:div#samples-container.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]
        [:div#prev-next-buttons.row.text-center
         [:button#prevButton.btn.btn-default {:style "display: none;"} "Prev"]
         [:button#nextButton.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))} "Next"]]]]

      (page/footer (:repos data))]

     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.start(" (:end data) "," (:version-id data) ");"]]))