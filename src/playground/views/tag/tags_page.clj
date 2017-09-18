(ns playground.views.tag.tags-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]))


;(defn divide-tags-by-blocks [tags]
;  (let [sorted-tags (sort-by clojure.string/lower-case compare tags)
;        blocks-tags (sort (group-by first sorted-tags))]
;    (prn blocks-tags)
;    (for [[letter tags] blocks-tags]
;      [:div.col-md-3.col-sm-4.col-xs-6
;       [:p.letter (if (<= (int \0) (int letter) (int \9))
;                    (str "1 — " letter)
;                    letter)]
;       [:ul
;        (for [tag tags]
;          [:li [:a {:href (str "/tags/" tag)}
;                (str tag)]])]])))

(defn divide-tags-by-blocks [tags]
  (let [sorted-tags (sort-by clojure.string/lower-case compare tags)

        blocks-tags (flatten (sort (group-by first sorted-tags)))]
    (for [letter-or-tag blocks-tags]
      (if (char? letter-or-tag)
        (let [letter letter-or-tag]
          [:p.letter (if (<= (int \0) (int letter) (int \9))
                       (str "1 — " letter)
                       letter)])
        [:a {:title (str "Tag - " letter-or-tag)
             :href  (str "/tags/" letter-or-tag)}
         (str letter-or-tag)]))))

(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper.tags-page

      (page/nav (:templates data) (:user data))

      [:div.intro
       [:div.container-fluid.content-container
        [:div.row
         [:div.col-sm-12
          [:div
           [:div.text
            [:h1
             [:b "Tags"]]
            [:p.description "It is an information resource that allows you to discover as many details<br>
            about any type of chart supported in our JavaScript (HTML5) charting libraries <br>
            as you need to make good use of it at ease and with full understanding. <br>
            Now, to get started, click on a chart category that you would like to explore."]]]]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:div.toggle-tabs.btn-group {:role "group"}
         [:a.active.btn.btn-link {:type "button"} "Popular tags"]
         [:a.btn.btn-link {:type "button"}
          [:span "Unknown tags"]]
         [:a.btn.btn-link {:type "button"}
          [:span "Knonw tags"]]]

        [:p.popular-label "Popular " [:b "tags"]]

        [:div.popular-tags-box
         (for [tag (take 13 (:all-tags data))]
           [:a.popular-tag-button
            {:title (str "Tag - " (:name tag))
             :href  (str "/tags/" (:name tag))}
            (:name tag)]
           )
         ]

        [:div.tags-box
         [:div.row
          [:div.col-sm-12.flex-content
           (divide-tags-by-blocks (map :name (:all-tags data)))]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))