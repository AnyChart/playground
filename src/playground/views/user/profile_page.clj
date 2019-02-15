(ns playground.views.user.profile-page
  (:require [playground.views.sample :as sample-view]
            [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]
            [playground.views.prev-next-buttons :as prev-next-buttons]
            [playground.elastic.core :as elastic]))

(defn pagination [page max-page end class]
  (prev-next-buttons/pagination "user-samples-prev"
                                "user-samples-next"
                                page
                                max-page
                                end
                                "/profile?page="
                                class))

(defn page [{{samples  :samples
              total    :total
              max-page :max-page
              end      :end} :result
              page            :page
              :as             data}]
  (def user (:user data)) 
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
        
        (pagination page max-page end "top")
        
        [:div#user-samples.samples-container.row.justify-content-between
         (sample-view/samples samples)
        ]

        (pagination page max-page end "bottom")
      (page/footer (:repos data) (:tags data) (:request-sets data))]
     page/jquery-script
     page/bootstrap-script
     page/site-script]))