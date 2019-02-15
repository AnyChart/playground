(ns playground.views.user.register-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "Registration | AnyChart Playground"})

    [:body
     page/body-tag-manager

     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div {:id "regContaner" :class "container"}
        [:div {:id "signup" :class "row justify-content-md-center align-items-center"}
          [:div {:class "col col-sm-11 col-md-6"}
            [:h2.popular-label.samples-label "Please sign up with"]
            
              [:ul {:id "reg" :class "list-group list-group-flush"}
                [:a {:href "https://accounts.google.com/o/oauth2/auth?access_type=offline&prompt=consent&redirect_uri=http://localhost:8081/sign_google&response_type=code&client_id=713517328581-rc2fikgk9kdsn07vfoohc6qffhauh849.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile"}
                  
                  [:li {:class "list-group-item"} 
                    [:span "Google"]
                  ]
                  ; [:div {:id "g-signin2" :data-onsuccess "onSignIn"} "Google"]
                ]
                [:a {:href "https://github.com/login/oauth/authorize?client_id=df3026bdfcaeeb79edf1&scope=user&redirect_uri=http://localhost:8081/sign_github"}
                 
                  [:li {:class "list-group-item"} 
                    [:span "GitHub"]
                  ]
                ]
              ]
            ; ]
          ]
        ]
      ]

      [:div.content
      ;  [:div.container-fluid.content-container

      ;   [:form.signup-form {:action "/signup" :method "POST"}
      ;    [:div.form-group
      ;     [:label {:for "exampleInputPassword1"} "Your name"]
      ;     [:input#exampleInputPassword1.form-control
      ;      {:placeholder "Name" :type "text" :name "fullname"}]]
      ;    [:div.form-group
      ;     [:label {:for "exampleInputPassword1"} "Username"]
      ;     [:input#exampleInputPassword1.form-control
      ;      {:placeholder "Username" :type "text" :name "username"}]]
      ;    [:div.form-group
      ;     [:label {:for "exampleInputEmail1"} "Email"]
      ;     [:input#exampleInputEmail1.form-control
      ;      {:placeholder "Email" :type "email" :name "email"}]]
      ;    [:div.form-group
      ;     [:label {:for "exampleInputPassword1"} "Password"]
      ;     [:input#exampleInputPassword1.form-control
      ;      {:placeholder "Password" :type "password" :name "password"}]]
      ;    [:button.btn.btn-default {:type "submit"} "Sign up"]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     page/jquery-script
     page/bootstrap-script
     page/site-script]))