(ns playground.views.marketing.data-sets-page
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
        [:h1.page-caption "Data Sets"]
        [:p.page-caption-desc "Тут будет overview предоставляемых источников данных."]

        (for [data-set (:all-data-sets data)]
          [:div.row.data-sets-item
           [:div.col-md-3.data-sets-item-icon
            [:img {:src (:logo data-set)}]]
           [:div.col-md-7
            [:h3 (:title data-set)]
            [:p (:description data-set)]
            (for [tag (:tags data-set)]
              [:span.label.label-primary.tag tag])]
           [:div.col-md-2
            [:a.btn.btn-primary.usage-sample-button {:href   (:sample data-set)
                                                     :target "_blank"} "Usage Sample"]]])]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))