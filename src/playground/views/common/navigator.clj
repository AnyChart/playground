(ns playground.views.common.navigator
  (:require [playground.utils.utils :as utils]
            [playground.views.common.left-menu :as left-menu-comp]
            [playground.views.common.search-bar :as search-bar-comp]))


(defn nav [templates user & [q]]
  [:header
   (left-menu-comp/left-menu-bg)
   (left-menu-comp/left-menu)
   (search-bar-comp/search-bar q)

   [:div.navbar-container

    [:div.container-fluid.content-container
     [:nav.navbar.navbar-expand-sm

      ;[:button.navbar-toggle.collapsed {:aria-controls "navbar"
      ;                                  :aria-expanded "false"
      ;                                  :data-target   "#navbar"
      ;                                  :data-toggle   "collapse"
      ;                                  :type          "button"}
      ; [:span.sr-only "Toggle navigation"]
      ; [:span.icon-bar]
      ; [:span.icon-bar]
      ; [:span.icon-bar]]
      ;

      [:div#bars-switcher.bars-switcher.d-md-none
       [:i.fas.fa-bars]]

      [:a.navbar-brand
       {:href "/" :title "Playground Home"}
       [:div.border-icon
        [:div.chart-row
         [:span.chart-col.green]
         [:span.chart-col.orange]
         [:span.chart-col.red]]]
       [:div.brand-label "AnyChart " [:b.hidden-extra-mobile "Playground"]]]

      ;; left navbar
      [:div.menu-line
       [:ul.navbar-nav.navbar-left.d-md-flex
        [:li.nav-item [:a.nav-link {:href "/chart-types" :title "Playground Chart Types"} "Chart Types"]]
        [:li.nav-item [:a.nav-link {:href "/tags" :title "Playground Tags"} "Tags"]]
        ;[:li [:a {:href "/datasets" :title "Playground Data Sets"} "Data Sets"]]
        [:li.nav-item.dropdown
         [:a.nav-link.dropdown-toggle {:href          "#"
                                       :id            "supportDropdown"
                                       :title         "Playground Support Submenu"
                                       :data-toggle   "dropdown"
                                       :role          "button"
                                       :aria-haspopup "true"
                                       :aria-expanded "false"} "Support"]
         [:div.dropdown-menu {:aria-labelledby "supportDropdown"}
          [:a.dropdown-item {:href "/support" :title "Playground Support"} "Support"]
          [:a.dropdown-item {:href "/roadmap" :title "Playground Roadmap"} "Roadmap"]
          [:a.dropdown-item {:href "/version-history" :title "Playground Version History"} "Version History"]]]
        ;[:li [:a {:href "/pricing" :title "Playground Pricing"} "Pricing"]]
        [:li.nav-item [:a.nav-link {:href "/about" :title "About Playground"} "About"]]
        ]

       ;; right navbar
       [:ul.navbar-nav.navbar-right.ml-auto

        ;[:li.search-box
        ; [:input#search-input.search {:type        "text"
        ;                              :placeholder "Search"
        ;                              :value       (or q "")}]
        ; [:span#search-input-icon.fas.fa-search]
        ; [:div#search-results-box.results {:style "display:none;"}
        ;  [:div#search-results]]]
        [:i#search-bar-open-icon.fas.fa-search]

        [:li.nav-item.dropdown
         [:a.nav-link.dropdown-toggle {:aria-expanded "false"
                                       :aria-haspopup "true"
                                       :role          "button"
                                       :data-toggle   "dropdown"
                                       :href          "#"} "Create"]
         [:div.dropdown-menu
          (for [template templates]
            [:a.dropdown-item {:href  (str "/new?template=" (:url template))
                               :title (str "Create " (:name template))}
             [:img {:src (str "/icons/" (utils/name->url (:name template)) ".svg")
                    :alt (str "Create " (:name template) " button icon")}]
             (:name template)])
          [:div.dropdown-divider]
          [:a.dropdown-item {:href "/new" :title "Create from scratch"}
           [:img {:src (str "/icons/from-scratch.svg")
                  :alt "Create from scratch button icon"}]
           "From scratch"]]]
        ]
       ]]]]])
