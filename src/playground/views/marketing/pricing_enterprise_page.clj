(ns playground.views.marketing.pricing-enterprise-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))


(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Pricing Enterprise | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body
     page/body-tag-manager

     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:h1.page-caption "Pricing - Enterprice"]
        [:p.page-caption-desc "Тут будет информация об использовании плейграунда в качесвте коробки.
        У гитхаба это выглядит вот так: https://enterprise.github.com/home"]]]


      (page/footer (:repos data) (:tags data) (:data-sets data))]
     page/jquery-script
     page/bootstrap-script
     page/site-script]))