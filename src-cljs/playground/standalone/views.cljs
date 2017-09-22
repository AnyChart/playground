(ns playground.standalone.views
  (:require [re-frame.core :as rf]))


(defn view []
  (let [sample @(rf/subscribe [:sample])]
    [:div.container-fluid.content-container.standalone-sample-page

     [:div.row
      [:div.col-sm-5
       [:div.info
        [:h1 (:name sample)]

        [:div {:dangerouslySetInnerHTML {:__html (:description sample)}}]

        [:div.popular-tags-box
         (for [tag (:tags sample)]
           ^{:key tag}
           [:a.popular-tag-button {:href  (str "/tags/" tag)
                                   :title (str "Tag " tag)} tag])]

        (when (seq (:styles sample))
          [:div
           [:h2.popular-label "Styles"]
           [:div.popular-tags-box
            (for [link (:styles sample)]
              ^{:key link}
              [:div.popular-tags-button-box
               [:a.popular-tag-button {::href link} link]])]])

        (when (seq (:scripts sample))
          [:div
           [:h2.popular-label "Scripts"]
           [:div.popular-tags-box
            (for [link (:scripts sample)]
              ^{:key link}
              [:div.popular-tag-button-box
               [:a.popular-tag-button {::href link} link]])]])]]

      [:div.col-sm-7
       [:div.iframe-standalone-box
        [:iframe.iframe-standalone {:sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                                    :allowTransparency "true"
                                    :allowFullScreen   "true"
                                    :src               @(rf/subscribe [:sample-iframe-url])}]]]]]))