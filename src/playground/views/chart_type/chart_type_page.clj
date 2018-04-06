(ns playground.views.chart-type.chart-type-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]
            [playground.views.sample :as sample-view]
            [playground.views.prev-next-buttons :as prev-next-buttons]
            [playground.site.pages.chart-type-page-utils :as chart-type-page-utils]))

(defn get-id [text]
  (string/replace (string/lower-case text) #" " "-"))

(defn page [{:keys [page tag] :as data} chart-type relations]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (chart-type-page-utils/title (:name chart-type) page)
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

        (prev-next-buttons/buttons "tag-samples-prev"
                                   "tag-samples-next"
                                   page
                                   (:end data)
                                   (str "/chart-types/" (:id chart-type) "?page="))

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     [:script (page/run-js-fn "playground.site.pages.chart_type_page.startChartTypePage"
                              (:end data)
                              page
                              tag
                              (:id chart-type)
                              (:name chart-type))]]))
