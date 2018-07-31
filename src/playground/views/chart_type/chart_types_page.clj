(ns playground.views.chart-type.chart-types-page
  (:require [playground.views.chart-type.common :as chart-type-common]
            [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))


(def ^:const chart-count 25)


(defn is-end [all-charts page]
  (let [pages (int (Math/ceil (/ all-charts chart-count)))]
    (>= page (dec pages))))


(defn page [data chart-types & [end page]]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Chart Types | AnyChart Playground"
                :description "Browse all available AnyChart Chart types by name or by the way chart is used. Click links below to proceed to study materials, samples selection, guides to similar chart types and other useful information."})
    [:body page/body-tag-manager
     [:div.wrapper.chart-types-page

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
             "Browse all available AnyChart Chart types by name or by "
             [:a {:title "Chart Types by Category" :href "/chart-types/categories"} "the way chart is used"]
             ". Click links below to proceed to study materials, samples selection, guides to similar chart types and other useful information."]]]]]]]


      [:div.content
       [:div.container-fluid.content-container

        [:div.elements-container
         [:div.tabs
          [:a.active {:title "Show all types chart"
                      :href  "/chart-types"}
           [:span "Show all types chart"]]
          [:a {:title "Group by usage type"
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

        (chart-type-common/chart-types-block chart-types)

        ;; Disable pagination
        ;[:div.prev-next-buttons
        ; [:a#tag-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
        ;                                                   :href  (str "/chart-types?page=" page)
        ;                                                   :title (str "Prev page, " page)}
        ;  [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
        ;  " Prev"]
        ; [:a#tag-samples-next.next-button.btn.btn-default {:style (str "display: " (if end "none;" "inline-block;"))
        ;                                                   :href  (str "/chart-types?page=" (-> page inc inc))
        ;                                                   :title (str "Next page, " (-> page inc inc))}
        ;  "Next "
        ;  [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     ;; Disable pagination
     ;[:script "playground.site.landing.startChartTypesPage(" end ", " page ");"]
     ]))