(ns playground.views.marketing.roadmap-page
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
        [:h1.page-caption "Roadmap"]
        [:p.page-caption-desc "Тут бутет информация о грядущих обновлениях и возможность зареквестить фичу."]]]

      (page/footer (:repos data))]]))