(ns playground.views.chart-type.common)


(defn chart-type-block [chart]
  [:div.text-center.chart-type-block.col
   [:a.chart
    {:title (:name chart)
     :href  (str "/chart-types/" (:id chart))}
    [:div.chart-img
     [:img {:alt (str "Chart type " (:name chart) " image")
            :src (:img chart)}]]
    [:span (:name chart)]]])


(defn chart-types-block [charts]
  [:div.row.chart-type-container
   (for [chart charts]
     (chart-type-block chart))
   (repeat 10 [:div.col {:style "min-width: 190px;"}])])