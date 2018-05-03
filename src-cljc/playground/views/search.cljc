(ns playground.views.search)


;(defn result-item [data]
;  [:div.search-result
;   [:a {:href  (:full-url data)
;        :title (:name data)}
;    (:name data)]])


(defn hint [hint-title]
  [:div.search-result
   [:a {:href   (str "/search?q=" hint-title)
        :target "_blank"
        :title  hint-title}
    hint-title]])