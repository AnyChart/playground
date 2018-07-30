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