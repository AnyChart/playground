(ns playground.views.chart-type.chart-types-category-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [clojure.string :as string]))

(defn page [data category]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (str (:name category) " | Chart Type Categories | AnyChart Playground")
                :description (page/desc (:description category))})
    [:body page/body-tag-manager
     [:div.wrapper.chart-types-category-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:div.category
         [:div.img-box
          [:img {:alt (str (:name category) " category image")
                 :src (:img category)}]]
         [:div.info
          [:h1.name.popular-label (:name category)]
          [:p.description (string/join "\n" (:description category))]]]

        [:div.row.chart-type-container
         (for [chart (:charts category)]
           [:div.col-md-15.col-sm-3.col-xs-4.col-xxs.col-xxxs.text-center.chart-type-block
            [:a.chart
             {:title (:name chart)
              :href  (str "/chart-types/" (:id chart))}
             [:div.chart-img
              [:img {:alt (str "Chart type " (:name chart) " image")
                     :src (:img chart)}]]
             [:span (:name chart)]]])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/site-script)

     ]))