(ns playground.views.chart-type.chart-types-category-page
  (:require [playground.views.chart-type.common :as chart-type-common]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [clojure.string :as string]))


(defn page [data category]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (str (:name category) " | Chart Type Categories | AnyChart Playground")
                :description (page/desc (:description category))})
    [:body
     page/body-tag-manager

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

        (chart-type-common/chart-types-block (:charts category))]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     page/jquery-script
     page/bootstrap-script
     page/site-script]))