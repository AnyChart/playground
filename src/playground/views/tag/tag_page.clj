(ns playground.views.tag.tag-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.tag-page-utils :as tag-page-utils]
            [playground.views.prev-next-buttons :as prev-next-buttons]
            [playground.data.tags :as tags-data]
            [clojure.string :as string]))


(defn search-query [tag]
  (if (string/includes? tag " ")
    (str "t:'" tag "' ")
    (str "t:" tag)))


(defn pagination [page max-page end tag class]
  (prev-next-buttons/pagination "tag-samples-prev"
                                "tag-samples-next"
                                page
                                max-page
                                end
                                (str "/tags/" (tags-data/original-name->id-name tag) "?page=")
                                class))


(defn page [{page            :page
             tag             :tag
             tag-data        :tag-data
             {samples  :samples
              total    :total
              max-page :max-page
              end      :end} :result
             :as             data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (tag-page-utils/title tag page)
                :description (page/desc (:description tag-data))})
    [:body page/body-tag-manager
     [:div.wrapper.tag-page

      (page/nav (:templates data)
                (:user data)
                (search-query tag))

      [:div.content
       [:div.container-fluid.content-container

        [:h1 [:b tag]]

        (when (seq (:description tag-data))
          [:p.tag-description (:description tag-data)])
        (when (seq (:description tag-data))
          [:h2.popular-label.samples-label "Samples"])

        (pagination page max-page end tag "top")
        [:div#tag-samples.row.samples-container
         (for [sample samples]
           (sample-view/sample-landing sample))]

        (pagination page max-page end tag "bottom")
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     [:script (page/run-js-fn "playground.site.pages.tag_page.startTagPage" page max-page end tag)]]))