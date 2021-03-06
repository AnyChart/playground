(ns playground.views.user.profile-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [{:keys [user] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "Profile | AnyChart Playground"})
    [:body
     page/body-tag-manager

     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:div.branches
         [:ul
          (for [version (:versions data)]
            [:li [:a {:href (str "/" (:name (:repo data)) "/" (:name version))}
                  (str "Branch " (:name version))]])]]
        [:p [:b "ID: "] (:id user)]
        [:p [:b "Username: "] (:username user)]
        [:p [:b "Full name: "] (:fullname user)]
        [:p [:b "Email: "] (:email user)]
        [:p [:b "Permissions: "] (:permissions user)]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     page/jquery-script
     page/bootstrap-script
     page/site-script]))