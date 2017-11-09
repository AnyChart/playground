(ns playground.views.tag.tag-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.tag-page-utils :as tag-page-utils]
            [playground.views.prev-next-buttons :as prev-next-buttons]))


(defn page [{:keys [page tag tag-data] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (tag-page-utils/title tag page)
                :description (page/desc (:description tag-data))})
    [:body page/body-tag-manager
     [:div.wrapper.tag-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1 [:b tag]]

        (when (seq (:description tag-data))
          [:p.tag-description (:description tag-data)])
        (when (seq (:description tag-data))
          [:h2 "Samples"])

        [:div#tag-samples.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]

        (prev-next-buttons/buttons "tag-samples-prev"
                                   "tag-samples-next"
                                   page
                                   (:end data)
                                   (str "/tags/" tag "?page="))
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script (page/run-js-fn "playground.site.pages.tag_page.startTagPage" (:end data) page tag)]]))