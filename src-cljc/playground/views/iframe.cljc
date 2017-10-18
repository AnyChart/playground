(ns playground.views.iframe
  (:require [playground.utils.utils :as utils]))


(defn iframe [sample]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:http-equiv "X-UA-Compatible"
            :content    "IE=edge"}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:title (:name sample)]
    [:link {:rel "canonical" :href (utils/full-canonical-url-iframe sample)}]

    (when (seq (:tags sample))
      [:meta {:name    "keywords"
              :content (clojure.string/join "," (:tags sample))}])

    [:meta {:name    "description"
            :content "AnyChart - JavaScript Charts designed to be embedded and integrated"}]
    "<!--[if lt IE 9]>\n<script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script>\n<script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\n<![endif]-->"

    (for [style (:styles sample)]
      [:link {:rel  "stylesheet"
              :type "text/css"
              :href style}])
    [:style (:style sample)]]

   [:body
    (:markup sample)

    (for [script (:scripts sample)]
      [:script {:src script}])

    [:script {:type "text/javascript"}
     (:code sample)]

    ]])