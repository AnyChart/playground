(ns playground.views.marketing.about-page
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
        [:h1.page-caption "About"]
        [:p.page-caption-desc "Рассказываем о проекте, о том что плейграунд это не просто очередной codepen,
        а место в котором можно созданны все условия чтобы заниматься визуализацией и аналитикой данных.
        Тут будут перечислены все поддерживаемые фичи плейграунда с красивыми текстами и иллюстрациями."]]]

      (page/footer (:repos data))]]))