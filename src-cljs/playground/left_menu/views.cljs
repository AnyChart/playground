(ns playground.sidemenu.views
  (:require [re-frame.core :as rf]))


(defn view []
  (when @(rf/subscribe [:left-menu/show])
    [:div.leftmenu.hide-outside
     [:span.glyphicon-remove.glyphicon.close
      {:on-click #(rf/dispatch [:left-menu/close])}]
     [:ul
      [:li [:a {:href "/chart-types"} "Chart Types"]]
      [:li [:a {:href "/tags"} "Tags"]]
      [:li [:a {:href "/datasets"} "Data Sets"]]
      [:li.dropdown
       [:a.dropdown-toggle {:data-toggle   "collapse"
                            :data-target   "#submenu1"
                            :aria-expanded false} "Support"
        [:span.caret]]
       [:ul.collapse {:id   "submenu1"
                      :role "menu"}
        [:li [:a {:href "/support"} "Support"]]
        [:li [:a {:href "/roadmap"} "Roadmap"]]
        [:li [:a {:href "/version-history"} "Version History"]]]]
      [:li [:a {:href "/pricing"} "Pricing"]]
      [:li [:a {:href "/about"} "About"]]]]))