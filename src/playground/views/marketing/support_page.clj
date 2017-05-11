(ns playground.views.marketing.support-page
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
        [:h1.page-caption "Support"]
        [:p.page-caption-desc "Тут будет информация о поддержке."]]]

      (page/footer (:repos data) (:tags data))]]))