(ns playground.core
  (:require [reagent.core :as reagent :refer [atom]]))


(defn app []
  [:div.container-fluid
   [:div.row
    [:div
     [:a.navbar-brand
      {:href "//anychart.com/"}]
     ]
    [:h2 "PG"]
    [:div "New pg !"]]
   ]
 )


(defn mount-html []
  (reagent/render-component [app] (.getElementById js/document "main"))
  )

(defn ^:export run []
  (mount-html))
