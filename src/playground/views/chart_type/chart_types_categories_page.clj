(ns playground.views.chart-type.chart-types-categories-page
  (:require [cheshire.core :as json]
            [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [clojure.string :as string]))


(defn page [data categories]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Chart Type Categories | AnyChart Playground"
                :description "Browse all available AnyChart Chart types by the way chart is used or by name. Click links below to proceed to study materials, samples selection, guides to similar chart types and other useful information."})
    [:body
     page/body-tag-manager

     [:div.wrapper.chart-types-categories-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1
             "Chart " [:b "Types"]]
            [:p.description
             "Browse all available AnyChart Chart types by the way chart is used or by "
             [:a {:title "Chart Types" :href "/chart-types"} "name"]
             ". Click links below to proceed to study materials, samples selection, guides to similar chart types and other useful information."
             ]]]]]]]


      [:div.content
       [:div.container-fluid.content-container

        [:div.elements-container
         [:div.tabs
          [:a {:title "Show all types chart"
               :href  "/chart-types"}
           [:span "Show all types chart"]]
          [:a.active {:title "Group by usage type"
                      :href  "/chart-types/categories"}
           [:span "Group by usage type"]]]

         [:div.toggle-tabs.btn-group {:role "group"}
          [:a.active.btn.btn-link {:type "button"} "Application"]
          [:a.btn.btn-link {:type "button"}
           [:span "Data formats"]]
          [:a.btn.btn-link {:type "button"}
           [:span "Popular"]]]

         [:div.search
          [:span.glyphicon.glyphicon-search]]]

        [:div.row.categories-container
         (for [category categories]
           [:div.col-sm-12
            [:div.category
             [:div.img-box
              [:img {:alt (str (:name category) " category image")
                     :src (:img category)}]]
             [:div.info
              [:p.name.popular-label (:name category)]
              [:p.description (string/join "\n" (:description category))]
              [:a.learn-more-label {:title (str "Learn more about " (:name category) " category")
                                    :href  (str "/chart-types/categories/" (:id category))} "Learn more"]]]])
         ]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     page/jquery-script
     page/bootstrap-script
     page/site-script]))