(ns playground.views.admin.admin-page
  (:require [hiccup.page :as hiccup-page]
            [playground.data.config :as c]
            [playground.views.common :as page]
            [playground.web.utils :as web-utils]))


(defn page-raw [data]
  (hiccup-page/html5
    {:lang "en"}
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
     [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]

     [:title "Version management panel"]
     [:link {:crossorigin "anonymous"
             :integrity   "sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
             :href        "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
             :rel         "stylesheet"}]
     [:link {:crossorigin "anonymous"
             :integrity   "sha384-O8whS3fhG2OnA5Kas0Y9l3cfpmYjapjI0E4theH4iuMD+pLhbf6JI0jIMfYcK3yZ"
             :href        "https://use.fontawesome.com/releases/v5.1.1/css/all.css",
             :rel         "stylesheet"}]
     [:link {:href "/css/admin.css" :rel "stylesheet"}]
     ]

    [:body
     [:div#main-container]
     [:script {:src "https://code.jquery.com/jquery-3.2.1.min.js"}]
     [:script {:crossorigin "anonymous"
               :integrity   "sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
               :src         "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"}]
     [:script
      {:crossorigin "anonymous"
       :integrity   "sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
       :src         "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"}]
     [:script {:src (str "/js/admin.js?v=" (c/commit)) :type "text/javascript"}]
     [:script {:type "text/javascript"}
      (page/run-js-fn "playground.admin.core.init" (web-utils/pack data))]
     ]

    )
  )


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Admin panel | AnyChart Playground"
                :description "Admin panel"})
    [:body page/body-tag-manager
     [:div.wrapper.tags-stat-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:p.popular-label [:b "Admin"] " panel"]
        [:div#main-container]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     (page/jquery-script)
     (page/bootstrap-script)
     ;(page/site-script)
     [:script {:src (str "/js/admin.js?v=" (c/commit)) :type "text/javascript"}]
     [:script {:type "text/javascript"}
      (page/run-js-fn "playground.admin.core.init" (web-utils/pack data))]
     [:link {:href "/css/admin.css" :rel "stylesheet"}]

     ]))