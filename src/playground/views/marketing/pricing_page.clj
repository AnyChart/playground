(ns playground.views.marketing.pricing-page
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
        [:h1.page-caption "Pricing"]
        [:p.page-caption-desc "Тут будет информация о персональных и командных тарифах (когда они появятся).
        А так же краткая иформация о коробочном использовании, которая будет вести на /pricing/enterprise"]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))