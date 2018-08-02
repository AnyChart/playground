(ns playground.views.tag.tags-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [playground.data.tags :as tags-data]
            [clojure.string :as string]))


;(defn divide-tags-by-blocks [tags]
;  (let [sorted-tags (sort-by string/lower-case compare tags)
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
  (let [sorted-tags (sort-by string/lower-case compare tags)
        blocks-tags (flatten (sort (group-by (fn [tag]
                                               (if (<= (int \0) (int (first tag)) (int \9))
                                                 \0
                                                 (first tag)))
                                             sorted-tags)))]
    (for [letter-tag blocks-tags]
      (cond
        (= letter-tag \0) [:p.letter "1 — 9"]
        (char? letter-tag) [:p.letter letter-tag]
        (string? letter-tag) [:a {:title (str "Tag - " letter-tag)
                                  :href  (str "/tags/" (tags-data/original-name->id-name letter-tag))}
                              (str letter-tag)]))))


(defn tags-height [tags cols-num]
  (let [tags-count (count tags)
        title-height 37
        title-count 27
        tags-height 19
        k (if (= cols-num 1) 1 1.02)]
    (int (* k (/ (+ (* title-height title-count)
                    (* tags-height tags-count))
                 cols-num)))))


(defn tags-style
  "Dynamically resize tags box height"
  [tags]
  (let [h1 (tags-height tags 1)
        h2 (tags-height tags 2)
        h3 (tags-height tags 3)
        h4 (tags-height tags 4)
        h5 (tags-height tags 5)]
    (format
      ".flex-content { height: %spx;}
       @media (min-width: 500px) { .flex-content { height: %spx;} }
       @media (min-width: 750px) { .flex-content { height: %spx;} }
       @media (min-width: 1000px) { .flex-content { height: %spx;} }
       @media (min-width: 1200px) { .flex-content { height: %spx;} }"
      h1 h2 h3 h4 h5)))


(defn page [data]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Tags | AnyChart Playground"
                :description "Each sample in the Playground has a set of tags attached. Click links below to proceed to the selection of samples with a given tag."})
    [:body page/body-tag-manager
     [:style (tags-style (:all-tags data))]
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
            [:p.description "Each sample in the Playground has a set of tags attached. Click links below to proceed to the selection of samples with a given tag."]]]]]]]

      [:div.content
       [:div.container-fluid.content-container

        [:div.elements-container
         [:div.toggle-tabs.btn-group {:role "group"}
          [:a.active.btn.btn-link {:type "button"} "Popular tags"]
          [:a.btn.btn-link {:type "button"}
           [:span "Unknown tags"]]
          [:a.btn.btn-link {:type "button"}
           [:span "Knonw tags"]]]
         [:div.search
          [:span.glyphicon.glyphicon-search]]]

        [:p.popular-label "Popular " [:b "tags"]]

        [:div.popular-tags-box
         (for [tag (take 13 (:all-tags data))]
           [:a.popular-tag-button
            {:title (str "Tag - " (:name tag))
             :href  (str "/tags/" (tags-data/original-name->id-name (:name tag)))}
            (:name tag)])]

        [:div.tags-box
         [:div.row
          [:div.col-sm-12.flex-content
           ;{:style (str "height: " (tags-height (:all-tags data) 1) "px")}
           (divide-tags-by-blocks (map :name (:all-tags data)))]]]]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     (page/jquery-script)
     (page/bootstrap-script)
     (page/site-script)
     ]))