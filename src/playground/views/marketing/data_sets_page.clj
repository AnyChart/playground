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
        [:div.row.data-sets-item
         [:div.col-md-3.data-sets-item-icon {:style "text-align:right;"}
          [:img {:src   "https://static.anychart.com/cdn/anydata/common/0.png"
                 :style "width: 100%; height: 100%; max-width: 200px; margin-top: 20px;"}]]
         [:div.col-md-7
          [:h3 "Top 10 Cosmetic Products by Revenue"]
          [:p "This data set provides a dummy data for single value charts demonstration"]
          [:span.label.label-primary {:style "marign-left: 4px;"} "Bar Charts"]
          [:span.label.label-primary {:style "marign-left: 4px;"} "Sales"]
          [:span.label.label-primary {:style "marign-left: 4px;"} "Revenue"]
          [:span.label.label-primary {:style "marign-left: 4px;"} "Dummy data"]
          [:span.label.label-primary {:style "marign-left: 4px;"} "Single value"]]
         [:div.col-md-2
          [:a.btn.btn-primary {:href "#" :style "display:block; margin-bottom:6px;"} "Usage Sample"]
          [:a.btn.btn-success {:href "#" :style "display:block;"} "Quick Add"]]]]]

      (page/footer (:repos data) (:tags data))]]))