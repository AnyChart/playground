(ns playground.views.common.create-buttons
  (:require [playground.utils.utils :as utils]))


(defn create-box-button [template hidden-small]
  [:a.create-button {:href  (str "/new?template=" (:url template))
                     :title (str "Create " (:name template))
                     :class (if hidden-small "d-sm-none d-xl-flex" "")
                     }
   [:img {:src (str "icons/" (utils/name->url (:name template)) ".svg")
          :alt (str "Create " (:name template) " button icon")}]
   [:div.text
    [:div.name [:b (:name template)]]
    [:div.template "template"]]])


(defn create-box [templates]
  [:div.create-buttons
   (for [template (take 4 templates)]
     (create-box-button template false))
   (create-box-button (last templates) true)

   [:a.create-button {:href  "/new"
                      :title (str "Create Other Types")}
    [:img {:src (str "icons/from-scratch.svg")
           :alt (str "Create Other Types button icon")}]
    [:div.text
     [:div.name [:b "Other Types"]]
     [:div.template "template"]]]])
