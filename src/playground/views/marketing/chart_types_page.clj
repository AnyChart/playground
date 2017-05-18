(ns playground.views.marketing.chart-types-page
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
        [:h1.page-caption "Chart Types"]
        [:p.page-caption-desc "Тут будут перечисленны все поддерживаемые типы графиков.
        По сути этот должен быть некий клон чартопедии, как-то сказать какие графики можно использовать - нужно,
        но клонить чартопедию для этого не хочется. http://www.anychart.com/chartopedia/"]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))