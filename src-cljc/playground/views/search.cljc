(ns playground.views.search)


(defn result-item [data]
  [:div.search-result
   [:a {:href  (:full-url data)
        :title (:name data)}
    (:name data)]])