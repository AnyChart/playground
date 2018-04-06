(ns playground.views.repo.repo-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn search-query [repo-name]
  (str "p:" repo-name " "))


(defn page [{repo :repo :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title (str (:title repo) " | AnyChart Playground")})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data)
                (:user data)
                (search-query (:name repo)))

      [:div.content
       [:div.container-fluid.content-container
        [:div.branches
         [:ul
          (for [version (:versions data)]
            [:li [:a {:href (str "/projects/" (:name repo) "/" (:name version))}
                  (str "Branch " (:name version))]])]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     ]))