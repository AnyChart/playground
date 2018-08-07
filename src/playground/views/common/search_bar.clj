(ns playground.views.common.search-bar)


(defn search-bar []
  [:div.search-container
   [:div.container-fluid.search.content-container

    [:div.search-area
     [:input {:placeholder "SEARCH"}
      [:i.icon.fas.fa-search]
      ]]

    ]])