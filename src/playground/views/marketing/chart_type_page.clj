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
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-md-12
          [:h1.page-caption (:name chart-type)]]]
        [:div.row.chart-type-page

         [:div.col-md-6
          [:h3 "Description"]
          [:div (string/trim (:description chart-type))]

          (when (seq relations)
            [:div
             [:h3 "Similar Charts"]
             (for [{:keys [name relations]} relations]
               [:a.btn.btn-default.btn-sm {:role  "button"
                                           :href  (:str "/chart-types/" (get-id name))
                                           :title (str "Similar by " (string/join ", " relations))} name])])

          [:div
           [:h3 "Resources"]
           [:div {:id "resources", :class "catalog-text"}
            [:p "View more samples in our gallery:"]
            [:ul
             (for [link (:pgLinks chart-type)]
               [:li [:a {:href link} link]])]
            [:p "Read more information in our documentation:"]
            [:ul
             (for [[k v] (:docsLinks chart-type)]
               [:li [:a {:href v} k]])]]]]

         [:div.col-md-6
          [:iframe {:style       "height:350px; width: 100%; border: 1px solid #DDDDDD;"
                    :class       "clear-iFrame"
                    :scrolling   "no"
                    :src         (str "http://playground.anychart.com/chartopedia-gallery/latest/samples/"
                                      (string/replace (:name chart-type) #" " "_")
                                      "-iframe")
                    :id          "pg-frame"
                    :frameborder "0"}]]]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))
