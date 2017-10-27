(ns playground.views.user.register-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "Registration | AnyChart Playground"})
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:form {:action "/signup" :method "POST"}
         [:div.form-group
          [:label {:for "exampleInputPassword1"} "Your name"]
          [:input#exampleInputPassword1.form-control
           {:placeholder "Name" :type "text" :name "fullname"}]]
         [:div.form-group
          [:label {:for "exampleInputPassword1"} "Username"]
          [:input#exampleInputPassword1.form-control
           {:placeholder "Username" :type "text" :name "username"}]]
         [:div.form-group
          [:label {:for "exampleInputEmail1"} "Email"]
          [:input#exampleInputEmail1.form-control
           {:placeholder "Email" :type "email" :name "email"}]]
         [:div.form-group
          [:label {:for "exampleInputPassword1"} "Password"]
          [:input#exampleInputPassword1.form-control
           {:placeholder "Password" :type "password" :name "password"}]]
         [:button.btn.btn-default {:type "submit"} "Sign up"]]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))