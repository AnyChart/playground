(ns playground.views.marketing.version-history-page
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
        [:h1.page-caption "Version History"]
        [:p.page-caption-desc "Тут будет вся история проекта."]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))