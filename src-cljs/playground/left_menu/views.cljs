(ns playground.left-menu.views
  (:require [re-frame.core :as rf]))


(defn view []
  (when @(rf/subscribe [:left-menu/show])
    [:div.leftmenu.hide-outside
     [:i#leftmenu-close.fas.fa-times {:on-click #(rf/dispatch [:left-menu/close])}]
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
                            :title         "Playground Support Submenu"
                            :on-click      #(rf/dispatch [:left-menu/support-toggle])} "Support"
        [:span.caret]]
       (when @(rf/subscribe [:left-menu/support-expand])
         [:ul.collapse {:id   "submenu1"
                        :role "menu"}
          [:li [:a {:href  "/support"
                    :title "Playground Support"} "Support"]]
          [:li [:a {:href  "/roadmap"
                    :title "Playground Roadmap"} "Roadmap"]]
          [:li [:a {:href  "/version-history"
                    :title "Playground Version History"} "Version History"]]])]
      ;[:li [:a {:href  "/pricing"
      ;          :title "Playground Pricing"} "Pricing"]]
      [:li [:a {:href  "/about"
                :title "About Playground"} "About"]]]]))