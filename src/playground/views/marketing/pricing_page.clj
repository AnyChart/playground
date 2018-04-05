(ns playground.views.marketing.pricing-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Pricing | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:h1.page-caption "Pricing"]
        [:p.page-caption-desc "Тут будет информация о персональных и командных тарифах (когда они появятся).
        А так же краткая иформация о коробочном использовании, которая будет вести на /pricing/enterprise"]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     (page/site-script)
     ]))