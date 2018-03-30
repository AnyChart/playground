(ns playground.search.views
  (:require [re-frame.core :as rf]
            [playground.views.search :as search-views]))


(defn search-window []
  (when @(rf/subscribe [:search/show])
    [:div#search-results-box.results
     [:div#search-results
      (let [samples @(rf/subscribe [:search/results])]
        (if (seq samples)
          (for [sample samples]
            ^{:key (str (:url sample) (:version-id sample))}
            [search-views/result-item sample])
          "Not found"))
      ]]))