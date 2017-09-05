(ns playground.views.marketing.chart-types-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]))

(defn get-chart-type [chart-type]
  (merge chart-type
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/chart-types/" (:id chart-type) ".json"))
           true)
         {:img (str "http://www.anychart.com/chartopedia/chart-types/" (:id chart-type) "/thumb.png")}))

(defn parse-data []
  (let [base (json/parse-string (slurp "resources/chartopedia/data/main.json") true)
        chart-types (map #(get-chart-type %) (:chartTypes base))
        relations (:relations base)]
    {:chart-types chart-types
     :relations   relations}))

(defmacro parse-data-compile-time []
  `'~(parse-data))

(def data (parse-data-compile-time))
(def chart-types (:chart-types data))
(def relations (:relations data))

(defn get-chart [name]
  (first (filter #(= (:id %) name) chart-types)))

(defn get-relations [chart]
  (let [result-relations (reduce (fn [res relation]
                                   (if (some #(= (:name chart) %) (:charts relation))
                                     (let [new-res (reduce (fn [res chart-name]
                                                             (if (not= chart-name (:name chart))
                                                               (update res chart-name conj (:name relation))
                                                               res))
                                                           res
                                                           (:charts relation))]
                                       new-res)
                                     res))
                                 {}
                                 relations)
        result-relations* (map (fn [[name relations]] {:name      name
                                                       :relations (sort relations)}) result-relations)]
    (sort-by :name result-relations*)))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:h1.page-caption "Chart Types"]
        [:p.page-caption-desc "It is an information resource that allows you to discover as many details about any type of chart supported in our JavaScript (HTML5) charting libraries as you need to make good use of it at ease and with full understanding. Now, to get started, click on a chart category that you would like to explore."]

        [:div.row
         (for [chart chart-types]
           [:div.col-lg-3.col-md-4.col-sm-6.chart-types-page.text-center
            [:a.chart {:href (str "/chart-types/" (:id chart))}
             [:span.chart-img
              [:img {:src (:img chart)}]]
             [:span (:name chart)]]])]

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))