(ns playground.views.chart-type.chart-type-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]
            [playground.views.sample :as sample-view]))

(defn get-id [text]
  (string/replace (string/lower-case text) #" " "-"))

(defn page [{:keys [page tag] :as data} chart-type relations]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (str (:name chart-type) " | Chart Types | AnyChart Playground")
                :description (page/desc (:description chart-type))})
    [:body page/body-tag-manager
     [:div.wrapper.chart-type-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:div.row.info
         [:div.col-md-6.column1
          [:h1 [:b (:name chart-type)]]
          [:div.description (string/trim (:description chart-type))]
          ]

         [:div.col-md-6.column2
          (when (seq relations)
            [:div
             [:h2 "Similar Charts"]
             [:div.popular-tags-box
              (for [{:keys [name relations]} relations]
                [:a.popular-tag-button {:href  (:str "/chart-types/" (get-id name))
                                        :title (str "Similar by " (string/join ", " relations))} name])]])
          [:div
           [:h2 "Resources"]
           [:div
            [:div.note "View more samples in our gallery:"
             (for [link (:pgLinks chart-type)]
               [:div [:a {:href link} link]])]
            [:div.note "Read more information in our documentation:"
             (for [[k v] (:docsLinks chart-type)]
               [:div [:a {:href v} k]])]]]
          ]
         ]

        ;[:iframe.clear-iFrame
        ; {:style       "height:350px; width: 100%; border: 1px solid #DDDDDD;"
        ;  :scrolling   "no"
        ;  :src         (str "http://playground.anychart.com/chartopedia-gallery/latest/samples/"
        ;                    (string/replace (:name chart-type) #" " "_")
        ;                    "-iframe")
        ;  :id          "pg-frame"
        ;  :frameborder "0"}]

        (when (seq (:samples data))
          [:h2.popular-label.samples-label "Samples"])

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
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.startTagPage(" (:end data) ", " page ", '" tag "', false);"]]))
