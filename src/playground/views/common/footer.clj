(ns playground.views.common.footer
  (:require [playground.data.tags :as tags-data]
            [clj-time.core :as t]))


(defn bottom-footer []
  [:div.footer-bottom-box
   [:div.footer-inner
    [:a.soc-network
     {:title  "AnyChart Facebook"
      :target "_blank"
      :rel    "nofollow"
      :href   "https://www.facebook.com/AnyCharts"}
     [:span.soc-network-icon.fb [:i.sn-mini-icon.ac.ac-facebook]]]
    [:a.soc-network
     {:title  "AnyChart Twitter"
      :target "_blank"
      :rel    "nofollow"
      :href   "https://twitter.com/AnyChart"}
     [:span.soc-network-icon.tw [:i.sn-mini-icon.ac.ac-twitter]]]
    [:a.soc-network
     {:title  "AnyChart LinkedIn"
      :target "_blank"
      :rel    "nofollow"
      :href   "https://www.linkedin.com/company/386660"}
     [:span.soc-network-icon.in [:i.sn-mini-icon.ac.ac-linkedin]]]]
   [:span.copyright (str "&copy; " (t/year (t/now)) " ")
    [:a {:href   "https://www.anychart.com"
         :rel    "nofollow"
         :target "_blank"} "AnyChart.com"]
    " All rights reserved."]])


(defn footer [repos tags data-sets]
  [:footer.footer
   [:div.container-fluid.content-container
    [:div.row.justify-content-center
     [:div.col-md-10.col-lg-10.col-xl-8
      [:div.row

       [:div.footer-block.col                               ;col-md-3.col-sm-6.col-xs-12
        [:div [:a.caption {:href "https://www.anychart.com" :title "AnyChart"} [:b "AnyChart"]]]
        [:div [:a {:href "https://www.anychart.com/features/" :title "AnyChart Features"} "Features"]]
        [:div [:a {:href "https://www.anychart.com/solutions/" :title "AnyChart Business Solutions"} "Business Solutions"]]
        [:div [:a {:href "https://www.anychart.com/technical-integrations/" :title "AnyChart Technical Integrations"} "Technical Integrations"]]
        [:div [:a {:href "https://www.anychart.com/chartopedia/" :title "AnyChart Chartopedia"} "Chartopedia"]]
        [:div [:a {:href "https://www.anychart.com/download/" :title "AnyChart Download"} "Download"]]
        [:div [:a {:href "https://www.anychart.com/buy/" :title "AnyChart Buy"} "Buy"]]
        [:div [:a {:href "https://www.anychart.com/blog/" :title "AnyChart Blog"} "Blog"]]]

       [:div.footer-block.col
        [:div [:a.caption {:href "/" :title "Playground Home"} [:b "Playground"]]]
        [:div [:a {:href "/chart-types" :title "Playground Chart Types"} "Chart Types"]]
        ;[:div [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]
        [:div [:a {:href "/support" :title "Playground Support"} "Support"]]
        [:div [:a {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]]
        [:div [:a {:href "/version-history" :title "Playground Version History"} "Version History"]]
        ;[:div [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
        [:div [:a {:href "/about" :title "About Playground"} "About"]]]

       ;[:div.w-100.d-block.d-sm-none]
       [:div.footer-block.col
        [:div [:a.caption {:href "/projects" :title "Playground Projects"} [:b "Projects"]]]
        (for [repo (remove :templates repos)]
          [:div [:a {:href  (str "/projects/" (:name repo))
                     :title (str "Projects - " (:title repo))}
                 (:title repo)]])]

       [:div.footer-block.col
        [:div [:a.caption {:href "/tags" :title "Playground Tags"} [:b "Tags"]]]
        (for [tag (sort-by :name tags)]
          [:div [:a {:href  (str "/tags/" (tags-data/original-name->id-name (:name tag)))
                     :title (str "Tags - " (:name tag))}
                 (:name tag)]])]

       ;; TODO: wait datasets text (for centering footer)

       ;[:div.col-sm-4.col-xs-8
       ; [:div [:a.caption {:href "/datasets" :title "Playground Data Sets"} [:b "Data Sets"]]]
       ; (for [data-set (sort-by :title data-sets)]
       ;   [:div.dataset [:a {:href  (str "/datasets/" (:data-source-name data-set) "/" (:name data-set))
       ;                      :title (str "Data Sets - " (:title data-set))}
       ;                  (:title data-set)]])]
       ]]]
    (bottom-footer)]])

