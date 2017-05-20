(ns playground.views.standalone-sample-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]
            [clj-time.core :as t]))

(defn page [{:keys [url sample] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data) sample)

      [:div.content
       [:div.iframe-standalone-box
        [:iframe.iframe-standalone {:sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                                    :allowtransparency "true"
                                    :allowfullscreen   "true"
                                    :src               url}]]]

      [:footer.footer
       [:div.container
        [:p.text-muted (str "&copy; " (t/year (t/now)) " AnyChart.com All rights reserved.")]]]

      ]]))
