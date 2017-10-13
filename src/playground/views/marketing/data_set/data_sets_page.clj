(ns playground.views.marketing.data-set.data-sets-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
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

        [:div.row
         (for [data-set (:all-data-sets data)]
           [:div.col-md-4
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
             ]
            ]
           )]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))