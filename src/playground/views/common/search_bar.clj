(ns playground.views.common.search-bar)


(defn search-bar [q]
  [:div#search-bar.search-container {:style "display:none;"}
   [:div.container-fluid.search.content-container

    [:div.search-area
     [:input#search-input {:value       (or q "")
                           :placeholder "SEARCH"}
      [:i#search-input-icon.icon.fas.fa-search]]

     [:i#search-close-icon.icon.fas.fa-times]]

    [:div#search-results-box.results {:style "display:none;"}
     [:div#search-results]]]])