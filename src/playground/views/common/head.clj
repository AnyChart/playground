(ns playground.views.common.head
  (:require [playground.views.common.resources :as resources]))


(defn head [data]
  [:head
   [:meta {:charset "UTF-8"}]
   ;[:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
   [:meta {:content "width=device-width, initial-scale=1, shrink-to-fit=no" :name "viewport"}]

   [:title (or (:title data) "AnyChart Playground")]
   [:meta {:property "og:title" :content (or (:title data) "AnyChart Playground")}]
   (when (seq (:description data)) [:meta {:property "og:description" :content (:description data)}])
   (when (seq (:description data)) [:meta {:name "description" :content (:description data)}])
   [:meta {:name "twitter:title" :content (or (:title data) "AnyChart Playground")}]
   [:meta {:name "author" :content (or (:author data) "AnyChart")}]

   "<!--[if lt IE 9]><script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script><script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script><![endif]-->"

   [:link {:href  "/apple-touch-icon.png"
           :sizes "180x180"
           :rel   "apple-touch-icon"}]
   [:link {:href  "/favicon-32x32.png"
           :sizes "32x32"
           :type  "image/png"
           :rel   "icon"}]
   [:link {:href  "/favicon-16x16.png"
           :sizes "16x16"
           :type  "image/png"
           :rel   "icon"}]
   [:link {:href "/manifest.json"
           :rel  "manifest"}]
   [:link {:color "#2c4b76"
           :href  "/safari-pinned-tab.svg"
           :rel   "mask-icon"}]
   [:meta {:content "playground.anychart.com"
           :name    "apple-mobile-web-app-title"}]
   [:meta {:content "playground.anychart.com"
           :name    "application-name"}]
   [:meta {:content "#2c4b76"
           :name    "theme-color"}]

   [:link {:rel "stylesheet" :type "text/css" :href "https://cdn.anychart.com/fonts/2.7.2/anychart.css"}]
   [:link {:rel "stylesheet" :type "text/css" :href "https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&amp;subset=greek"}]
   [:link {:crossorigin "anonymous"
           :integrity   "sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ"
           :href        "https://use.fontawesome.com/releases/v5.2.0/css/all.css"
           :rel         "stylesheet"}]


   ;[:script {:src "/jquery/jquery.min.js"}]
   ;[:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
   (if (System/getProperty "local")
     resources/bootstrap-style-link
     [:style {:type "text/css"} resources/bootstrap-style])
   (if (System/getProperty "local")
     resources/main-style-link
     [:style {:type "text/css"} resources/main-style])
   resources/head-tag-manager])