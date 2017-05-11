(ns playground.views.marketing.pricing-enterprise-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container
        [:h1.page-caption "Pricing - Enterprice"]
        [:p.page-caption-desc "Тут будет информация об использовании плейграунда в качесвте коробки.
        У гитхаба это выглядит вот так: https://enterprise.github.com/home"]]]


      (page/footer (:repos data) (:tags data))]]))