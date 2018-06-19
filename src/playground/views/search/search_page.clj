(ns playground.views.search.search-page
  (:require [playground.views.common :as page]
            [playground.site.pages.search-page-utils :as search-page-utils]
            [hiccup.page :as hiccup-page]
            [playground.views.sample :as sample-view]
            [playground.views.prev-next-buttons :as prev-next-buttons]))


(defn page [{q               :q
             page            :page
             {samples  :samples
              total    :total
              max-page :max-page
              end      :end} :result
             :as             data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (search-page-utils/title q page)
                :description "AnyChart Playground search page"})
    [:body page/body-tag-manager
     [:div.wrapper.tag-page

      (page/nav (:templates data)
                (:user data)
                q)

      [:div.content
       [:div.container-fluid.content-container
        [:h1 [:b "Search result for: "] q]

        [:div#search-samples.row.samples-container
         (for [sample samples]
           (sample-view/sample-landing sample))]

        (prev-next-buttons/pagination "search-samples-prev"
                                      "search-samples-next"
                                      page
                                      max-page
                                      end
                                      (str "/search?q=" q "&page="))
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     [:script (page/run-js-fn "playground.site.pages.search_page.startSearchPage" page max-page end total q)]
     ]))