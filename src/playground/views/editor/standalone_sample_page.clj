(ns playground.views.editor.standalone-sample-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [clojure.string :as string]
            [hiccup.page :as hiccup-page]
            [clj-time.core :as t]))

;; Deprecated: shown by editor
(defn page [{:keys [url sample] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title "AnyChart Playground"})
    [:body page/body-tag-manager
     [:div.wrapper.standalone-sample-page

      (page/nav (:templates data) (:user data) sample)

      [:div.content

       [:div.container-fluid.content-container

        [:div.row
         [:div.col-sm-5
          [:div.info
           [:h1 (:name sample)]

           [:div (:description sample)]

           [:div.popular-tags-box
            (for [tag (:tags sample)]
              [:a.popular-tag-button {:href  (str "/tags/" tag)
                                      :title (str "Tag " tag)} tag])]

           (when (seq (:styles sample))
             [:div
              [:h2.popular-label "Styles"]
              [:div.popular-tags-box
               (for [link (:styles sample)]
                 [:div.popular-tags-button-box
                  [:a.popular-tag-button {::href link} link]])]])

           (when (seq (:scripts sample))
             [:div
              [:h2.popular-label "Scripts"]
              [:div.popular-tags-box
               (for [link (:scripts sample)]
                 [:div.popular-tag-button-box
                  [:a.popular-tag-button {::href link} link]])]])]]

         [:div.col-sm-7
          [:div.iframe-standalone-box
           [:iframe.iframe-standalone {:sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                                       :allowtransparency "true"
                                       :allowfullscreen   "true"
                                       :src               url}]]]

         ]]]

      (page/bottom-footer)]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))
