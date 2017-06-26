(ns playground.views.marketing.chart-types-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]
            [cheshire.core :as json]
            [clojure.string :as string]))

(defn get-id [text]
  (string/replace (string/lower-case text) #" " "-"))


(defn get-chart-types [chart-types]
  (map (fn [chart-type] {:id   (get-id chart-type)
                         :name chart-type}) chart-types))

(defn get-chart-type [chart-type]
  (merge chart-type
         (json/parse-string
           (slurp
             (str "resources/chartopedia/data/chart-types/" (:id chart-type) ".json"))
           true)
         {:img (str "http://www.anychart.com/chartopedia/chart-types/" (:id chart-type) "/thumb.png")}))

(defn parse-chart-types []
  (let [base (json/parse-string (slurp "resources/chartopedia/data/main.json") true)
        ;chart-types (get-chart-types (:chartTypes base))
        chart-types (map #(get-chart-type %) (:chartTypes base))]
    (vec chart-types)))

(defmacro parse-chart-types-const [] (parse-chart-types))

(def chart-types (parse-chart-types-const))

(defn get-chart [name]
  (first (filter #(= (:id %) name) chart-types)))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container
        [:h1.page-caption "Chart Types"]
        [:p.page-caption-desc "It is an information resource that allows you to discover as many details about any type of chart supported in our JavaScript (HTML5) charting libraries as you need to make good use of it at ease and with full understanding. Now, to get started, click on a chart category that you would like to explore."]

        (for [chart chart-types]
          [:div.col-lg-3.col-md-4.col-sm-6.chart-types-page.text-center
           [:a.chart {:href (str "/chart-types/" (:id chart))}
            [:span.chart-img
             [:img {:src (:img chart)}]]
            [:span (:name chart)]]])

        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]]))