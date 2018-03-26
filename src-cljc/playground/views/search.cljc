(ns playground.views.search)


(defn result-item [data]
  [:div.search-result
   [:a {:href (:full-url data)}
    (:name data)]])