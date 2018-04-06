(ns playground.views.repo.repos-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "Projects | AnyChart Playground"})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:div.branches
         [:ul
          (for [repo (remove :templates (:repos data))]
            [:li [:a {:href (str "/projects/" (:name repo))}
                  (str (:title repo))]])]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     ]))
