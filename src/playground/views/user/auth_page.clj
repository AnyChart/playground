(ns playground.views.user.auth-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "Authorizaton | AnyChart Playground"})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:form {:action "/signin" :method "POST"}
         [:div.form-group
          [:label {:for "exampleInputEmail1"} "Username or email"]
          [:input#exampleInputEmail1.form-control
           {:placeholder "Username or email" :type "text" :name "username"}]]
         [:div.form-group
          [:label {:for "exampleInputPassword1"} "Password"]
          [:input#exampleInputPassword1.form-control
           {:placeholder "Password" :type "password" :name "password"}]]
         [:button.btn.btn-default {:type "submit"} "Log in"]]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]]))