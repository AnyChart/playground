(ns playground.standalone.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [playground.data.tags :as tags-data]))


(defn iframe-standalone []
  (reagent/create-class {:component-did-mount #(rf/dispatch [:on-update-iframe])
                         :reagent-render      (fn []
                                                [:iframe {:id                "result-iframe"
                                                          :name              "result-iframe"
                                                          :class             "iframe-standalone"
                                                          :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                                                          :allowTransparency "true"
                                                          :allowFullScreen   "true"
                                                          ;:src               @(rf/subscribe [:sample-iframe-url])
                                                          }])}))


(defn view []
  (let [sample @(rf/subscribe [:sample])]
    [:div.container-fluid.content-container.standalone-sample-page

     [:div.row
      [:div.col-sm-5
       [:div.info
        [:h1 (:name sample)]

        [:div {:dangerouslySetInnerHTML {:__html (cond (seq (:description sample)) (:description sample)
                                                       (seq (:short-description sample)) (:short-description sample)
                                                       :else "")}}]

        [:div.popular-tags-box
         (for [tag (:tags sample)]
           ^{:key tag}
           [:a.popular-tag-button {:href  (str "/tags/" (tags-data/original-name->id-name tag))
                                   :title (str "Tag " tag)} tag])]

        (when (seq (:styles sample))
          [:div
           [:h2.popular-label "Styles"]
           [:div.popular-tags-box
            (for [link (:styles sample)]
              ^{:key link}
              [:div.popular-tag-button-box
               [:a.popular-tag-button {:title link
                                       :href  link} link]])]])

        (when (seq (:scripts sample))
          [:div
           [:h2.popular-label "Scripts"]
           [:div.popular-tags-box
            (for [link (:scripts sample)]
              ^{:key link}
              [:div.popular-tag-button-box
               [:a.popular-tag-button {:title link
                                       :href  link} link]])]])]]

      [:div.col-sm-7
       [:div.iframe-standalone-box
        [iframe-standalone]]]]]))