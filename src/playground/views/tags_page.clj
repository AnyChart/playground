(ns playground.views.tags-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn divide-tags-by-blocks [tags]
  (let [sorted-tags (sort-by clojure.string/lower-case compare tags)
        blocks-tags (partition 10 10 [] sorted-tags)]
    (for [tags blocks-tags]
      [:div.col-md-3.col-sm-4.col-xs-6
       [:ul
        (for [tag tags]
          [:li [:a {:href (str "/tags/" tag)}
                (str tag)]])]])))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container

        [:div.content-wrapper
         [:div.row.flex-content
          (divide-tags-by-blocks (map :name (:all-tags data)))]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))