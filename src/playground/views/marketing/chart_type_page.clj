(ns playground.views.marketing.chart-type-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]))

(defn get-id [text]
  (string/replace (string/lower-case text) #" " "-"))

(defn page [data chart-type relations]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
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

        [:iframe {:style       "height:350px; width: 100%; border: 1px solid #DDDDDD;"
                  :class       "clear-iFrame"
                  :scrolling   "no"
                  :src         (str "http://playground.anychart.com/chartopedia-gallery/latest/samples/"
                                    (string/replace (:name chart-type) #" " "_")
                                    "-iframe")
                  :id          "pg-frame"
                  :frameborder "0"}]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))
