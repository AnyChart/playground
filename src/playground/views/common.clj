(ns playground.views.common
  (:require [clj-time.core :as t]
            [playground.web.auth-base :as auth-base]
            [playground.utils.utils :as utils]
            [clojure.java.io :as io]))

(def main-style (slurp (io/resource "public/css/main.css")))

(defn head []
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
   [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]
   [:title "AnyChart Playground"]
   "<!--[if lt IE 9]><script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script><script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script><![endif]-->"
   "<!-- Latest compiled and minified CSS and Optional theme-->"
   [:link {:href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" :rel "stylesheet"}]
   ;[:link {:href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" :rel "stylesheet"}]
   [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"}]
   "<!-- Latest compiled and minified JavaScript -->"
   [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"}]
   (if (System/getProperty "local")
     [:link {:href "/css/main.css" :rel "stylesheet"}]
     [:style main-style])])

(defn nav-sample-menu-item [sample]
  [:li {:class "dropdown"}
   [:a {:href          "#"
        :class         "dropdown-toggle"
        :data-toggle   "dropdown"
        :role          "button"
        :aria-haspopup "true"
        :aria-expanded "false"} "View"
    [:span {:class "caret"}]]
   [:ul {:class "dropdown-menu"}
    [:li [:a {:href (str (utils/sample-url sample) "?view=editor")} "Editor"]]
    [:li [:a {:href (str (utils/sample-url sample) "?view=standalone")} "Standalone"]]
    [:li [:a {:href (str (utils/sample-url sample) "?view=iframe")} "Iframe"]]]])

(defn nav [templates user & [sample]]
  [:nav.navbar.navbar-default
   [:div.container-fluid

    [:div.navbar-header
     [:button.navbar-toggle.collapsed {:aria-controls "navbar"
                                       :aria-expanded "false"
                                       :data-target   "#navbar"
                                       :data-toggle   "collapse"
                                       :type          "button"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand
      {:href "/"}
      [:img {:alt    "AnyChart"
             :height "30"
             :width  "30"
             :style  "display:inline-block"
             :src    "/icons/anychart.png"}] "AnyChart Playground "]]

    ;; left navbar
    [:div#navbar.navbar-collapse.collapse
     [:ul.nav.navbar-nav

      [:li [:a {:href "/chart-types"} "Chart Types"]]
      [:li [:a {:href "/tags"} "Tags"]]
      [:li [:a {:href "/datasets"} "Data Sets"]]

      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "Support"
        [:span {:class "caret"}]]

       [:ul {:class "dropdown-menu"}
        [:li [:a {:href "/support"} "Support"]]
        [:li [:a {:href "/roadmap"} "Roadmap"]]
        [:li [:a {:href "/version-history"} "Version History"]]]]
      [:li [:a {:href "/pricing"} "Pricing"]]
      [:li [:a {:href "/about"} "About"]]

      (when sample
        (nav-sample-menu-item sample))]

     ;; right navbar
     [:ul.nav.navbar-nav.navbar-right

      [:li.dropdown
       [:a.dropdown-toggle {:aria-expanded "false"
                            :aria-haspopup "true"
                            :role          "button"
                            :data-toggle   "dropdown"
                            :href          "#"} "Create"
        [:span.caret]]
       [:ul.dropdown-menu
        (for [template templates]
          [:li [:a {:href (str "/new?template=" (:url template))} (:name template)]])
        [:li.divider {:role "separator"}]
        [:li [:a {:href "/new"} "From scratch"]]]]
      (if (auth-base/can user :signin)
        [:li [:a {:href "/signin"} "Log In"]]
        [:li [:a {:href "/signout"} "Log Out"]])
      (when (auth-base/can user :signup)
        [:li [:a {:href "/signup"} "Sign Up"]])

      ]]]])


(defn jumbotron [templates]
  [:div.content-container.row
   [:div.col-lg-12.col-md-12
    [:div.jumbotron {:style "margin-top: 20px;"}
     [:h1 "AnyChart Playground "]
     [:p "AnyChart Playground is a place where all your data visualization dreams come true"]
     [:p
      (for [template templates]
        [:button.btn.btn-primary.btn-lg
         {:role    "button"
          :onclick (str "location.href='/new?template=" (:url template) "';")}
         (str "Create " (:name template))])]]]])


(defn footer [repos tags data-sets]
  [:footer.footer
   [:div.container
    [:div.row

     [:div.col-sm-2.col-xs-4
      [:div [:b "Vendor"]]
      [:div [:a {:href "https://anychart.com"} "AnyChart"]]
      [:div [:a {:href "https://anychart.com/"} "Products"]]
      [:div [:a {:href "https://anychart.com/features"} "Features"]]
      [:div [:a {:href "https://anychart.com/"} "Resources"]]
      [:div [:a {:href "https://anychart.com/download"} "Download"]]
      [:div [:a {:href "https://anychart.com/buy"} "Buy"]]
      [:div [:a {:href "https://anychart.com/blog"} "Blog"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:b "Playground"]]
      [:div [:a {:href "/chart-types"} "Chart Types"]]
      [:div [:a {:href "/datasets"} "Data Sets"]]
      [:div [:a {:href "/support"} "Support"]]
      [:div [:a {:href "/roadmap"} "Roadmap"]]
      [:div [:a {:href "/version-history"} "Version History"]]
      [:div [:a {:href "/pricing"} "Pricing"]]
      [:div [:a {:href "/about"} "About"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:b "Projects"]]
      (for [repo (remove :templates repos)]
        [:div [:a {:href (str "/projects/" (:name repo))} (:title repo)]])]

     [:div.clearfix.visible-xs-block]

     [:div.col-sm-2.col-xs-4
      [:div [:b "Tags"]]
      (for [tag tags]
        [:div [:a {:href (str "/tags/" (:name tag))} (:name tag)]])]

     [:div.col-sm-2.col-xs-4
      [:div [:b "Data Sets"]]
      (for [data-set data-sets]
        [:div.dataset [:a {:href  (str "/datasets/" (:data-source-name data-set) "/" (:name data-set))
                           :title (:title data-set)}
                       (:title data-set)]])]

     [:div.col-sm-2.col-xs-4
      [:div [:b "Social"]]
      [:div [:a {:href "https://www.facebook.com/AnyCharts"} "Facebook"]]
      [:div [:a {:href "https://twitter.com/AnyChart"} "Twitter"]]
      [:div [:a {:href "https://www.linkedin.com/company/386660"} "Linked In"]]]

     ]
    [:p.text-muted (str "&copy; " (t/year (t/now)) " AnyChart.com All rights reserved.")]
    ]])