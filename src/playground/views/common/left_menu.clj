(ns playground.views.common.left-menu)


(defn left-menu-bg []
  [:div#leftmenu-bg.d-md-none])


(defn left-menu []
  [:div#leftmenu.leftmenu.hide-outside.d-md-none {:style "visibility: hidden;"}
   [:span.glyphicon-remove.glyphicon.close]
   [:i#leftmenu-close.fas.fa-times]
   ;; [:div#leftmenu-bg]
   [:ul
    [:li [:a {:href  "/chart-types"
              :title "Playground Chart Types"} "Chart Types"]]
    [:li [:a {:href  "/tags"
              :title "Playground Tags"} "Tags"]]
    ;[:li [:a {:href  "/datasets"
    ;          :title "Playground Data Sets"} "Data Sets"]]
    [:li.dropdown
     [:a.dropdown-toggle {:data-toggle   "collapse"
                          :data-target   "#submenu1"
                          :aria-expanded false
                          :title         "Playground Support Submenu"} "Support"
      [:span.caret]]
     [:ul.collapse {:id   "submenu1"
                    :role "menu"}
      [:li [:a {:href  "/support"
                :title "Playground Support"} "Support"]]
      [:li [:a {:href  "/roadmap"
                :title "Playground Roadmap"} "Roadmap"]]
      [:li [:a {:href  "/version-history"
                :title "Playground Version History"} "Version History"]]]]
    ;[:li [:a {:href  "/pricing"
    ;          :title "Playground Pricing"} "Pricing"]]
    [:li [:a {:href  "/about"
              :title "About Playground"} "About"]]]])
