(ns playground.views.repo.version-page
  (:require [playground.views.common :as page]
            [playground.views.sample :as sample-view]
            [hiccup.page :as hiccup-page]
            [playground.site.pages.version-page-utils :as version-page-utils]))



(defn page [{:keys [repo version page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title (version-page-utils/title (:name version) page (-> data :repo :title))})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:p.popular-label "Version " [:b "samples"]]
        [:div#version-samples.row.samples-container
         (for [sample (:samples data)]
           (sample-view/sample-landing sample))]
        [:div.prev-next-buttons
         [:a#version-samples-prev.prev-button.btn.btn-default {:style (str "display: " (if (zero? page) "none;" "inline-block;"))
                                                               :href  (str "/" (:name repo) "/" (:name version) "?page=" page)
                                                               :title (str "Prev page, " page)}
          [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
          " Prev"]
         [:a#version-samples-next.next-button.btn.btn-default {:style (str "display: " (if (:end data) "none;" "inline-block;"))
                                                               :href  (str "/" (:name repo) "/" (:name version) "?page=" (-> page inc inc))
                                                               :title (str "Next page, " (-> page inc inc))}
          "Next "
          [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
     [:script {:src "/js/site.js" :type "text/javascript"}]
     [:script "playground.site.pages.version_page.startVersionPage("
      (:end data) ", "
      page ","
      (-> data :version :id) ",'"
      (:name version) "','"
      (-> data :repo :title)
      "');"]]))