(ns playground.views.repo.version-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.version-page-utils :as version-page-utils]
            [playground.views.prev-next-buttons :as prev-next-buttons]))


(defn search-query [repo-name version-name]
  (str "p:" repo-name " v:" version-name " "))


(defn pagination [page max-page end repo version class]
  (prev-next-buttons/pagination "version-samples-prev"
                                "version-samples-next"
                                page
                                max-page
                                end
                                (str "/projects/" (:name repo) "/" (:name version) "?page=")
                                class))


(defn page [{repo            :repo
             version         :version
             page            :page
             {samples  :samples
              total    :total
              max-page :max-page
              end      :end} :result
             :as             data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title (version-page-utils/title (:name version) page (-> data :repo :title))})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data)
                (:user data)
                (search-query (:name repo) (:name version)))

      [:div.content
       [:div.container-fluid.content-container
        [:p.popular-label "Version " [:b "samples"]]

        (pagination page max-page end repo version "top")

        [:div#version-samples.row.samples-container
         (for [sample samples]
           (sample-view/sample-landing sample))]

        (pagination page max-page end repo version "bottom")]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     [:script (page/run-js-fn "playground.site.pages.version_page.startVersionPage"
                              page
                              max-page
                              end
                              total
                              (-> data :version :id)
                              (:name version)
                              (-> data :repo :title))]]))