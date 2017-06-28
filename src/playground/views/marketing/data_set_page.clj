(ns playground.views.marketing.data-set-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page data-set] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container
        [:h1.page-caption "Data Set"]
        [:div.row.data-sets-item
         [:div.col-md-3.data-sets-item-icon
          [:img {:src (:logo data-set)}]]
         [:div.col-md-7
          [:h3 (:title data-set)]
          [:p (:description data-set)]
          (for [tag (:tags data-set)]
            [:span.label.label-primary.tag tag])]
         [:div.col-md-2
          [:a.btn.btn-primary.usage-sample-button {:href "#"} "Usage Sample"]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))