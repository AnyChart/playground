(ns playground.views.tags-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid
        [:div.branches
         [:ul
          (for [tag (:all-tags data)]
            [:li [:a {:href (str "/tags/" (:name tag))}
                  (str  (:name tag))]])]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))