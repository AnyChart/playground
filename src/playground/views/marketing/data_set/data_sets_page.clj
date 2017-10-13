(ns playground.views.marketing.data-set.data-sets-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(def ^:const datasets-count 6)

(defn is-end [all-datasets page]
  (let [pages (int (Math/ceil (/ all-datasets datasets-count)))]
    (>= page (dec pages))))

(defn page [data end page]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
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

        [:div.toggle-tabs.btn-group {:role "group"}
         [:a.active.btn.btn-link {:type "button"} "Application"]
         [:a.btn.btn-link {:type "button"}
          [:span "Data formats"]]
         [:a.btn.btn-link {:type "button"}
          [:span "Popular"]]]

        [:div.row.datasets-container
         (let [current-datasets (nth (partition datasets-count datasets-count [] (:all-data-sets data)) page)]
           (for [data-set (:all-data-sets data)]
            [:div.col-md-4
             {:style (str "display: " (if (some (partial = data-set) current-datasets)
                                        "block;" "none;"))}
             [:div.item
              [:a {:title (:title data-set)
                   :href  (str "/datasets/" (:name data-set))}
               [:img {:src (:logo data-set)}]]
              [:p.title (:title data-set)]
              [:p.description (:description data-set)]

              [:div.popular-tags-box
               (for [tag (:tags data-set)]
                 [:a.popular-tag-button {:href  (str "/tags/" tag "s")
                                         :title (str tag)} tag])]

              [:a.quick-add-btn {:href   (:sample data-set)
                                 :target "_blank"} "Usage Sample"]
              [:a.learn-more {:title "Learn more"
                              :href  (str "/datasets/" (:name data-set))}
               [:span "Learn more"]]
              ]]
            ))]

        [:div.prev-next-buttons
         [:a#tag-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                                           :href  (str "/datasets?page=" page)
                                                           :title (str "Prev page, " page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#tag-samples-next.next-button.btn.btn-default {:style (str "display: " (if end "none;" "inline-block;"))
                                                           :href  (str "/datasets?page=" (-> page inc inc))
                                                           :title (str "Next page, " (-> page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.landing.startDatasetsPage(" end ", " page ");"]]))