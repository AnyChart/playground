(ns playground.views.data-set.data-sets-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [playground.site.pages.datasets-page-utils :as datasets-page-utils]
            [playground.views.prev-next-buttons :as prev-next-buttons]))

(def ^:const datasets-count 6)

(defn is-end [all-datasets page]
  (let [pages (int (Math/ceil (/ all-datasets datasets-count)))]
    (>= page (dec pages))))

(defn page-datasets [page data]
  (let [blocks (partition datasets-count datasets-count [] (:all-data-sets data))]
    (when (< page (count blocks))
      (nth blocks page))))


(defn page [data end page page-datasets]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (datasets-page-utils/title page)
                :description "The place where all your data visualization dreams come true"})
    [:body page/body-tag-manager
     [:div.wrapper.datasets-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1
             "Data " [:b "Sets"]]
            [:p.description "Need text here<br>
            about any type of chart supported in our JavaScript (HTML5) charting libraries <br>
            as you need to make good use of it at ease and with full understanding. <br>
            Now, to get started, click on a data set that you would like to explore."]]]]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:div.elements-container
         [:div.toggle-tabs.btn-group {:role "group"}
          [:a.active.btn.btn-link {:type "button"} "Application"]
          [:a.btn.btn-link {:type "button"}
           [:span "Data formats"]]
          [:a.btn.btn-link {:type "button"}
           [:span "Popular"]]]
         [:div.search
          [:span.glyphicon.glyphicon-search]]]

        [:div.row.datasets-container

         (for [data-set (:all-data-sets data)]
           [:div.col-md-4
            {:style (str "display: " (if (some (partial = data-set) page-datasets)
                                       "block;" "none;"))}
            [:div.item
             [:a {:title (:title data-set)
                  :href  (str "/datasets/" (:name data-set))}
              [:img {:alt (str (:title data-set) " - " (:description data-set))
                     :src (:logo data-set)}]]
             [:a.title {:title (:title data-set)
                        :href  (str "/datasets/" (:name data-set))}
              (:title data-set)]
             [:p.description (:description data-set)]

             [:div.popular-tags-box
              (for [tag (:tags data-set)]
                [:a.popular-tag-button {:href  (str "/tags/" tag)
                                        :title (str tag)} tag])]

             [:a.quick-add-btn {:title  (str (:title data-set) " usage sample")
                                :href   (:sample data-set)
                                :target "_blank"} "Usage Sample"]
             [:a.learn-more {:title (str "Learn more about " (:title data-set))
                             :href  (str "/datasets/" (:name data-set))}
              [:span "Learn more"]]
             ]]
           )]

        (prev-next-buttons/buttons "tag-samples-prev"
                                   "tag-samples-next"
                                   page
                                   end
                                   "/datasets?page=")
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     (page/site-script)
     [:script (page/run-js-fn "playground.site.pages.datasets_page.startDatasetsPage" end page)]]))