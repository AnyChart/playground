(ns playground.views.repo.repo-page
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
       [:div.container-fluid.content-container
        [:div.branches
         [:ul
          (for [version (:versions data)]
            [:li [:a {:href (str "/projects/" (:name (:repo data)) "/" (:name version))}
                  (str "Branch " (:name version))]])]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))