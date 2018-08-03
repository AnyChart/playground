(ns playground.views.data-set.data-set-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]))


(defn get-data [data-set]
  (let [data (:data data-set)
        lines (string/split-lines data)
        max-lines 14
        data (string/join "\n" (take max-lines lines))
        ;(json/generate-string (:data data-set) {:pretty true})
        ]
    (str data (if (> (count lines) max-lines) "\n..." ""))))


(defn get-code [data-set]
  (str "anychart.data.loadJsonFile('" (:url data-set) "', function(data) {
    // write your code here
);
"))


(defn page [{:keys [page data-set] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       (str (:title data-set) " | Data Sets | AnyChart Playground")
                :description (page/desc (:description data-set))})
    [:body
     page/body-tag-manager

     [:div.wrapper.dataset-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:div.row
         [:div.col-sm-6.column1
          [:h1.page-caption (:title data-set)]
          [:p (:description data-set)]

          [:p "To use this data set add following JavaScript Code to your page"]

          [:p.caption "Data from this set"]
          [:p [:pre (get-data data-set)]]

          [:p.caption "Tags"]
          [:div.popular-tags-box
           (for [tag (:tags data-set)]
             [:a.popular-tag-button {:href  (str "/tags/" tag)
                                     :title (str tag)} tag])]
          ]

         [:div.col-sm-6.column2
          [:a {:title  (str (:title data-set) " usage sample")
               :href   (:sample data-set)
               :target "_blank"}
           [:img {:alt (str (:title data-set) " - " (:description data-set))
                  :src (:logo data-set)}]]

          [:p.caption "Usage code"]
          [:textarea#myTextarea (get-code data-set)]

          [:div.line-box
           [:span "See Data "
            [:a.details {:title "Data Sets usage article"
                         :href  "https://docs.anychart.com/Working_with_Data/Using_Data_Sets"}
             [:span "Sets usage article"]]
            " for more details"]
           [:a.quick-add-btn {:title  (str (:title data-set) " usage sample")
                              :href   (:sample data-set)
                              :target "_blank"} "Usage Sample"]]
          ]
         ]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]

     [:script {:src "/codemirror/lib/codemirror.js"}]
     [:script {:src "/codemirror/mode/javascript/javascript.js"}]
     [:link {:rel "stylesheet" :href "/codemirror/lib/codemirror.css"}]
     [:script "
        var editor = CodeMirror.fromTextArea(myTextarea, {
           lineNumbers: false,
           mode: 'javascript'
        });
     "]
     page/jquery-script
     page/bootstrap-script
     page/site-script]))