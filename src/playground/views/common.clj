(ns playground.views.common
  (:require [clj-time.core :as t]
            [playground.web.auth-base :as auth-base]
            [playground.utils.utils :as utils]
            [clojure.java.io :as io]
            [clojure.string :as string]))


(def main-style (slurp (io/resource "public/css/main.css")))
(def bootstrap-style
  (string/replace
    (slurp (io/resource "public/bootstrap-3.3.7-dist/css/bootstrap.min.css"))
    #"\.\.\/fonts"
    "/bootstrap-3.3.7-dist/fonts"))


(defn head []
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
   [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]
   [:title "AnyChart Playground"]
   "<!--[if lt IE 9]><script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script><script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script><![endif]-->"

   [:link {:href  "/apple-touch-icon.png"
           :sizes "180x180"
           :rel   "apple-touch-icon"}]
   [:link {:href  "/favicon-32x32.png"
           :sizes "32x32"
           :type  "image/png"
           :rel   "icon"}]
   [:link {:href  "/favicon-16x16.png"
           :sizes "16x16"
           :type  "image/png"
           :rel   "icon"}]
   [:link {:href "/manifest.json"
           :rel  "manifest"}]
   [:link {:color "#2c4b76"
           :href  "/safari-pinned-tab.svg"
           :rel   "mask-icon"}]
   [:meta {:content "playground.anychart.com"
           :name    "apple-mobile-web-app-title"}]
   [:meta {:content "playground.anychart.com"
           :name    "application-name"}]
   [:meta {:content "#2c4b76"
           :name    "theme-color"}]

   [:link {:rel "stylesheet" :type "text/css" :href "https://cdn.anychart.com/fonts/2.7.2/anychart.css"}]
   [:link {:rel "stylesheet" :type "text/css" :href "https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&amp;subset=greek"}]

   ;[:script {:src "/jquery/jquery.min.js"}]
   ;[:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]
   (if (System/getProperty "local")
     [:link {:rel "stylesheet" :type "text/css" :href "/bootstrap-3.3.7-dist/css/bootstrap.min.css"}]
     [:style {:type "text/css"} bootstrap-style])
   (if (System/getProperty "local")
     [:link {:rel "stylesheet" :type "text/css" :href "/css/main.css"}]
     [:style {:type "text/css"} main-style])])


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


(defn nav-old [templates user & [sample]]
  [:nav.navbar.navbar-default
   [:div.container-fluid.content-container

    [:row.content-container
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
              :height "26"
              :width  "26"
              :style  "display:inline-block"
              :src    "/logo400x400.png"}]
       "AnyChart "
       [:b "Playground"]]]

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

       ;(if (auth-base/can user :signin)
       ;  [:li [:a {:href "/signin"} "Log In"]]
       ;  [:li [:a {:href "/signout"} "Log Out"]])
       ;(when (auth-base/can user :signup)
       ;  [:li [:a {:href "/signup"} "Sign Up"]])

       ]]]]])




(defn nav [templates user & [sample]]
  [:header
   [:div.container-fluid.content-container.header
    [:div.row
     [:div.col-sm-12
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
        {:href "/" :title "Playground Home"}
        [:img {:alt    "AnyChart"
               :height "26"
               :width  "26"
               :style  "display:inline-block"
               :src    "/logo400x400.png"}]
        "AnyChart "
        [:b "Playground"]]]

      ;; left navbar
      [:div#navbar.navbar-collapse.collapse
       [:ul.nav.navbar-nav

        [:li [:a {:href "/chart-types" :title "Playground Chart Types"} "Chart Types"]]
        [:li [:a {:href "/tags" :title "Playground Tags"} "Tags"]]
        [:li [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]

        [:li {:class "dropdown"}
         [:a {:href          "#"
              :title         "Playground Support Submenu"
              :class         "dropdown-toggle"
              :data-toggle   "dropdown"
              :role          "button"
              :aria-haspopup "true"
              :aria-expanded "false"} "Support"
          [:span {:class "caret"}]]

         [:ul {:class "dropdown-menu"}
          [:li [:a {:href "/support" :title "Playground Support"} "Support"]]
          [:li [:a {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]]
          [:li [:a {:href "/version-history" :title "Playground Version History"} "Version History"]]]]
        [:li [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
        [:li [:a {:href "/about" :title "About Playground"} "About"]]

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
            [:li
             [:a {:href  (str "/new?template=" (:url template))
                  :title (str "Create " (:name template))}
              [:img {:src (str "icons/" (utils/name->url (:name template)) ".svg")
                     :alt (str "Create " (:name template) " button icon")}]
              (:name template)]])
          [:li.divider {:role "separator"}]
          [:li
           [:a {:href "/new"
                :title "Create from scratch"}
            [:img {:src (str "icons/from-scratch.svg")
                   :alt "Create from scratch button icon"}]
            "From scratch"]]]]
        ]]]]]])






(defn jumbotron [templates]
  [:div
   [:div.text
    [:h1 "AnyChart "
     [:b "Playground"]]
    [:p.description "is a place where all your data visualization dreams come true"]]
   ])

(defn create-box [templates]
  [:div.create-buttons
   (for [template templates]
     [:a.create-button {:href  (str "/new?template=" (:url template))
                        :title (str "Create " (:name template))}
      [:img {:src (str "icons/" (utils/name->url (:name template)) ".svg")
             :alt (str "Create " (:name template) " button icon")}]
      [:div.text
       [:div.create "create"]
       [:div.name [:b (:name template)]]]])])


(defn footer [repos tags data-sets]
  [:footer.footer
   [:div.container-fluid.content-container
    [:div.row

     [:div.col-sm-2.col-xs-4
      [:div [:a {:href "https://www.anychart.com" :title "AnyChart" :class "caption"} [:b "Vendor"]]]
      [:div [:a {:href "https://www.anychart.com" :title "AnyChart"} "AnyChart"]]
      [:div [:a {:href "https://www.anychart.com/features" :title "AnyChart Features"} "Features"]]
      [:div [:a {:href "https://www.anychart.com/solutions/" :title "AnyChart Business Solutions"} "Business Solutions"]]
      [:div [:a {:href "https://www.anychart.com/technical-integrations/" :title "AnyChart Technical Integrations"} "Technical Integrations"]]
      [:div [:a {:href "https://www.anychart.com/chartopedia/" :title "AnyChart Chartopedia"} "Chartopedia"]]
      [:div [:a {:href "https://www.anychart.com/download" :title "AnyChart Download"} "Download"]]
      [:div [:a {:href "https://www.anychart.com/buy" :title "AnyChart Buy"} "Buy"]]
      [:div [:a {:href "https://www.anychart.com/blog" :title "AnyChart Blog"} "Blog"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:a {:href "/" :title "Playground Home" :class "caption"} [:b "Playground"]]]
      [:div [:a {:href "/chart-types" :title "Playground Chart Types"} "Chart Types"]]
      [:div [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]
      [:div [:a {:href "/support"  :title "Playground Support"} "Support"]]
      [:div [:a {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]]
      [:div [:a {:href "/version-history" :title "Playground Version History"} "Version History"]]
      [:div [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
      [:div [:a {:href "/about" :title "About Playground"} "About"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:a {:href "/projects" :title "Playground Projects" :class "caption"} [:b "Projects"]]]
      (for [repo (remove :templates repos)]
        [:div [:a {:href (str "/projects/" (:name repo))} (:title repo)]])]

     [:div.clearfix.visible-xs-block]

     [:div.col-sm-2.col-xs-4
      [:div [:a {:href "/tags" :title "Playground Tags" :class "caption"} [:b "Tags"]]]
      (for [tag tags]
        [:div [:a {:href (str "/tags/" (:name tag))} (:name tag)]])]

     [:div.col-sm-4.col-xs-8
      [:div [:a {:href "/datasets" :title "Playground Data Sets" :class "caption"} [:b "Data Sets"]]]
      (for [data-set data-sets]
        [:div.dataset [:a {:href  (str "/datasets/" (:data-source-name data-set) "/" (:name data-set))
                           :title (:title data-set)}
                       (:title data-set)]])]]
    [:div.footer-bottom-box
     [:div.footer-inner
      [:a.soc-network
       {:target "_blank" :rel "nofollow" :href "https://www.facebook.com/AnyCharts"}
       [:span.soc-network-icon.fb [:i.sn-mini-icon.ac.ac-facebook]]]
      [:a.soc-network
       {:target "_blank" :rel "nofollow" :href "https://twitter.com/AnyChart"}
       [:span.soc-network-icon.tw [:i.sn-mini-icon.ac.ac-twitter]]]
      [:a.soc-network
       {:target "_blank" :rel "nofollow" :href "https://www.linkedin.com/company/386660"}
       [:span.soc-network-icon.in [:i.sn-mini-icon.ac.ac-linkedin]]]]
     [:span.copyright (str "&copy; " (t/year (t/now)) " AnyChart.com All rights reserved.")]]]])