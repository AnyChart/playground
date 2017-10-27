(ns playground.views.chart-type.chart-types-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(def ^:const chart-count 25)

(defn is-end [all-charts page]
  (let [pages (int (Math/ceil (/ all-charts chart-count)))]
    (>= page (dec pages))))

(defn page [data chart-types end page]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Chart Types | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
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
            [:p.description "It is an information resource that allows you to discover as many details<br>
            about any type of chart supported in our JavaScript (HTML5) charting libraries <br>
            as you need to make good use of it at ease and with full understanding. <br>
            Now, to get started, click on a chart category that you would like to explore."]]]]]]]


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

        [:div.row.chart-type-container
         ;; Disable pagination
         ;let [current-charts (nth (partition chart-count chart-count [] chart-types) page)]
         (for [chart chart-types]
           [:div.col-md-15.col-sm-3.col-xs-4.col-xxs.col-xxxs.text-center.chart-type-block
            ;{:style (str "display: " (if (some (partial = chart) current-charts)
            ;                           "block;" "none;"))}
            [:a.chart
             {:title (:name chart)
              :href  (str "/chart-types/" (:id chart))}
             [:div.chart-img
              [:img {:alt (str "Chart type " (:name chart) " image")
                     :src (:img chart)}]]
             [:span (:name chart)]]])]

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
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     ;; Disable pagination
     ;[:script {:src "/js/site.js" :type "text/javascript"}]
     ;[:script "playground.site.landing.startChartTypesPage(" end ", " page ");"]
     ]))