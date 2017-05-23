(ns playground.views.repos-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (prn "tagspage" data)
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
          (for [repo (remove :templates (:repos data))]
            [:li [:a {:href (str "/projects/" (:name repo))}
                  (str (:title repo))]])]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))
