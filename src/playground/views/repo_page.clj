(ns playground.views.repo-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data))

      [:div.content
       [:div.container-fluid
        [:div.branches
         [:ul
          (for [version (:versions data)]
            [:li [:a {:href (str "/" (:name (:repo data)) "/" (:name version))} (str "Branch " (:name version))]])]]]]

      (page/footer (:repos data))]]))