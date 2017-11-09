(ns playground.views.common
  (:require [clj-time.core :as t]
            [playground.web.auth-base :as auth-base]
            [playground.utils.utils :as utils]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def head-tag-manager "<!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-5B8NXZ');</script>
<!-- End Google Tag Manager -->")

(def body-tag-manager "<!-- Google Tag Manager (noscript) -->
<noscript>
<iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-5B8NXZ\" height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->")


(defn desc [text]
  (when (seq text)
    (let [text (-> text
                   (string/replace #"<br/>" " ")
                   (string/replace #"<[^>]*>" ""))
          words (string/split (subs text 0 (min (count text) 160)) #" ")
          result (reduce (fn [res part]
                           (if (empty? res)
                             part
                             (if (< (count (str res " " part)) 155)
                               (str res " " part)
                               res))) "" words)]
      (string/trim result))))


(def main-style (slurp (io/resource "public/css/main.css")))
(def bootstrap-style
  (string/replace
    (slurp (io/resource "public/bootstrap-3.3.7-dist/css/bootstrap.min.css"))
    #"\.\.\/fonts"
    "/bootstrap-3.3.7-dist/fonts"))


(defn run-js-fn [fn-name & params]
  (str fn-name "("
       (->> params
            (map #(if (string? %)
                    (str "'" % "'")
                    %))
            (string/join ","))
       ");"))


(defn head [data]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
   [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]

   [:title (or (:title data) "AnyChart Playground")]
   [:meta {:property "og:title" :content (or (:title data) "AnyChart Playground")}]
   (when (seq (:description data)) [:meta {:property "og:description" :content (:description data)}])
   (when (seq (:description data)) [:meta {:name "description" :content (:description data)}])
   [:meta {:name "twitter:title" :content (or (:title data) "AnyChart Playground")}]
   [:meta {:name "author" :content (or (:author data) "AnyChart")}]

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
     [:style {:type "text/css"} main-style])
   head-tag-manager])


(defn nav-sample-menu-item [sample]
  [:li.dropdown
   [:a.dropdown-toggle {:href          "#"
                        :data-toggle   "dropdown"
                        :role          "button"
                        :aria-haspopup "true"
                        :aria-expanded "false"} "View"
    [:span.caret]]
   [:ul.dropdown-menu
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

       [:li.dropdown
        [:a.dropdown-toggle {:href          "#"
                             :data-toggle   "dropdown"
                             :role          "button"
                             :aria-haspopup "true"
                             :aria-expanded "false"} "Support"
         [:span.caret]]

        [:ul.dropdown-menu
         [:li [:a {:href "/support"} "Support"]]
         [:li [:a {:href "/roadmap"} "Roadmap"]]
         [:li [:a {:href "/version-history"} "Version History"]]]]
       ;[:li [:a {:href "/pricing"} "Pricing"]]
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
        ;[:li [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]

        [:li.dropdown
         [:a.dropdown-toggle {:href          "#"
                              :title         "Playground Support Submenu"
                              :data-toggle   "dropdown"
                              :role          "button"
                              :aria-haspopup "true"
                              :aria-expanded "false"} "Support"
          [:span.caret]]

         [:ul.dropdown-menu
          [:li [:a {:href "/support" :title "Playground Support"} "Support"]]
          [:li [:a {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]]
          [:li [:a {:href "/version-history" :title "Playground Version History"} "Version History"]]]]
        ;[:li [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
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
              [:img {:src (str "/icons/" (utils/name->url (:name template)) ".svg")
                     :alt (str "Create " (:name template) " button icon")}]
              (:name template)]])
          [:li.divider {:role "separator"}]
          [:li
           [:a {:href  "/new"
                :title "Create from scratch"}
            [:img {:src (str "/icons/from-scratch.svg")
                   :alt "Create from scratch button icon"}]
            "From scratch"]]]]
        ]]]]]])


(defn create-box [templates]
  [:div.create-buttons
   (for [template (take 4 templates)]
     [:a.create-button {:href  (str "/new?template=" (:url template))
                        :title (str "Create " (:name template))}
      [:img {:src (str "icons/" (utils/name->url (:name template)) ".svg")
             :alt (str "Create " (:name template) " button icon")}]
      [:div.text
       [:div.name [:b (:name template)]]
       [:div.template "template"]]])
   [:a.create-button {:href  "/new"
                      :title (str "Create Other Types")}
    [:img {:src (str "icons/from-scratch.svg")
           :alt (str "Create Other Types button icon")}]
    [:div.text
     [:div.name [:b "Other Types"]]
     [:div.template "template"]]]])


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
    [:div.row

     ;; TODO: wait datasets text (for centering footer)
     [:div.col-sm-2.col-xs-4]
     [:div.col-sm-2.col-xs-4
      [:div [:a.caption {:href "https://www.anychart.com" :title "AnyChart"} [:b "AnyChart"]]]
      [:div [:a {:href "https://www.anychart.com/features/" :title "AnyChart Features"} "Features"]]
      [:div [:a {:href "https://www.anychart.com/solutions/" :title "AnyChart Business Solutions"} "Business Solutions"]]
      [:div [:a {:href "https://www.anychart.com/technical-integrations/" :title "AnyChart Technical Integrations"} "Technical Integrations"]]
      [:div [:a {:href "https://www.anychart.com/chartopedia/" :title "AnyChart Chartopedia"} "Chartopedia"]]
      [:div [:a {:href "https://www.anychart.com/download/" :title "AnyChart Download"} "Download"]]
      [:div [:a {:href "https://www.anychart.com/buy/" :title "AnyChart Buy"} "Buy"]]
      [:div [:a {:href "https://www.anychart.com/blog/" :title "AnyChart Blog"} "Blog"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:a.caption {:href "/" :title "Playground Home"} [:b "Playground"]]]
      [:div [:a {:href "/chart-types" :title "Playground Chart Types"} "Chart Types"]]
      [:div [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]
      [:div [:a {:href "/support" :title "Playground Support"} "Support"]]
      [:div [:a {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]]
      [:div [:a {:href "/version-history" :title "Playground Version History"} "Version History"]]
      ;[:div [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
      [:div [:a {:href "/about" :title "About Playground"} "About"]]]

     [:div.col-sm-2.col-xs-4
      [:div [:a.caption {:href "/projects" :title "Playground Projects"} [:b "Projects"]]]
      (for [repo (remove :templates repos)]
        [:div [:a {:href  (str "/projects/" (:name repo))
                   :title (str "Projects - " (:title repo))}
               (:title repo)]])]

     [:div.clearfix.visible-xs-block]

     [:div.col-sm-2.col-xs-4
      [:div [:a.caption {:href "/tags" :title "Playground Tags"} [:b "Tags"]]]
      (for [tag (sort-by :name tags)]
        [:div [:a {:href  (str "/tags/" (:name tag))
                   :title (str "Tags - " (:name tag))}
               (:name tag)]])]

     ;[:div.col-sm-4.col-xs-8
     ; [:div [:a.caption {:href "/datasets" :title "Playground Data Sets"} [:b "Data Sets"]]]
     ; (for [data-set (sort-by :title data-sets)]
     ;   [:div.dataset [:a {:href  (str "/datasets/" (:data-source-name data-set) "/" (:name data-set))
     ;                      :title (str "Data Sets - " (:title data-set))}
     ;                  (:title data-set)]])]
     ]
    (bottom-footer)]])

