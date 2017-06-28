(ns playground.settings-window.views
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]))

(defn settings-window []
  (when @(rf/subscribe [:settings/show])
    [:div.settings-window
     [:div.settings-window-background {:on-click #(rf/dispatch [:settings/hide])}]
     [:div.settings-window-container


      [:ul.nav.nav-tabs.settings-tabs
       [:li {:class (when @(rf/subscribe [:settings/general-tab?]) "active")}
        [:a {:href     "javascript:;"
             :on-click #(rf/dispatch [:settings/general-tab])} "General"]]

       [:li {:class (when @(rf/subscribe [:settings/external-tab?]) "active")}
        [:a {:href     "javascript:;"
             :on-click #(rf/dispatch [:settings/external-tab])} "External Resources"]]

       [:li {:class (when @(rf/subscribe [:settings/data-sets-tab?]) "active")}
        [:a {:href     "javascript:;"
             :on-click #(rf/dispatch [:settings/data-sets-tab])} "Data Sets"]]]
      [:form

       (when @(rf/subscribe [:settings/general-tab?])

         [:div
          [:div.form-group
           [:label {:for "settings-name"} "Name"]
           [:input.form-control {:id            "settings-name"
                                 :default-value @(rf/subscribe [:name])
                                 :on-change     #(rf/dispatch [:settings/change-name (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "settings-short-desc"} "Short Description"]
           [:input.form-control {:id            "settings-short-desc"
                                 :default-value @(rf/subscribe [:short-description])
                                 :on-change     #(rf/dispatch [:settings/change-short-desc (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "settings-desc"} "Description"]
           [:textarea.form-control {:id            "settings-desc"
                                    :default-value @(rf/subscribe [:description])
                                    :on-change     #(rf/dispatch [:settings/change-desc (-> % .-target .-value)])}]]
          [:div.form-group
           [:label {:for "settings-tags"} "Tags"]
           [:textarea.form-control {:id        "settings-tags"
                                    :on-change #(rf/dispatch [:settings/change-tags (-> % .-target .-value)])
                                    :value     @(rf/subscribe [:tags-str])}]]


          ;[:div.form-inline
          ; {:style {:padding-right "10px"}}
          ;
          ; [:div.form-group
          ;  [:label {:for "settings-markup-type"} "Markup type"]
          ;  [:select.form-control {:id            "settings-markupt-type"
          ;                         :default-value @(rf/subscribe [:markup-type])}
          ;   [:option "HTML"]
          ;   [:option "Slim"]
          ;   [:option "Pug"]]]
          ;
          ; [:div.form-group
          ;  [:label {:for "settings-style-type"} "Style type"]
          ;  [:select.form-control {:id            "settings-style-type"
          ;                         :default-value @(rf/subscribe [:style-type])}
          ;   [:option "CSS"]
          ;   [:option "Sass"]
          ;   [:option "LESS"]]]
          ;
          ; [:div.form-group
          ;  [:label {:for "settings-code-type"} "Code type"]
          ;  [:select.form-control {:id            "settings-code-type"
          ;                         :default-value @(rf/subscribe [:code-type])}
          ;   [:option "JavaScript"]
          ;   [:option "CoffeeScript"]
          ;   [:option "TypeScript"]]]]

          ])


       (when @(rf/subscribe [:settings/external-tab?])
         [:div
          [:div.form-group
           [:label {:for "settings-styles"} "Styles"]
           [:textarea.form-control {:id        "settings-styles"
                                    :on-change #(rf/dispatch [:settings/change-styles (-> % .-target .-value)])
                                    :value     @(rf/subscribe [:styles-str])}]]

          [:div.form-group
           [:label {:for "settings-scripts"} "Scripts"]
           [:textarea.form-control {:id        "settings-scripts"
                                    :on-change #(rf/dispatch [:settings/change-scripts (-> % .-target .-value)])
                                    :value     @(rf/subscribe [:scripts-str])}]]

          [:h4 "Quick Add"]

          [:div.form-group
           [:label {:for "settings-select-bin"} "Binaries"]
           [:select.form-control {:id        "settings-select-bin"
                                  :on-change #(do (rf/dispatch [:settings/add-script (-> % .-target .-value)])
                                                  ;(set! (-> % .-target .-selectedIndex) 0)
                                                  )
                                  :on-focus  #(set! (-> % .-target .-selectedIndex) -1)}
            [:option {:value "https://cdn.anychart.com/js/latest/anychart-bundle.min.js"} "AnyChart"]
            [:option {:value "https://cdn.anychart.com/js/latest/data-adapter.min.js"} "Data Adapter"]]]

          [:div.form-group
           [:label {:for "settings-select-theme"} "Themes"]
           [:select.form-control {:id        "settings-select-theme"
                                  :on-change #(rf/dispatch [:settings/add-script (-> % .-target .-value)])
                                  :on-focus  #(set! (-> % .-target .-selectedIndex) -1)}
            [:option {:value "https://cdn.anychart.com/themes/latest/coffee.min.js"} "Coffee"]
            [:option {:value "https://cdn.anychart.com/themes/latest/dark_blue.min.js"} "Dark Blue"]
            [:option {:value "https://cdn.anychart.com/themes/latest/dark_earth.min.js"} "Dark Earth"]
            [:option {:value "https://cdn.anychart.com/themes/latest/dark_glamour.min.js"} "Dark Glamour"]
            [:option {:value "https://cdn.anychart.com/themes/latest/dark_provence.min.js"} "Dark Provence"]
            [:option {:value "https://cdn.anychart.com/themes/latest/dark_turquoise.min.js"} "Dark Turquoise"]
            [:option {:value "https://cdn.anychart.com/themes/latest/defaultTheme.min.js"} "Default Theme"]
            [:option {:value "https://cdn.anychart.com/themes/latest/light_blue.min.js"} "Light Blue"]
            [:option {:value "https://cdn.anychart.com/themes/latest/light_earth.min.js"} "Light Earth"]
            [:option {:value "https://cdn.anychart.com/themes/latest/light_glamour.min.js"} "Light Glamour"]
            [:option {:value "https://cdn.anychart.com/themes/latest/light_provence.min.js"} "Light Provence"]
            [:option {:value "https://cdn.anychart.com/themes/latest/light_turquoise.min.js"} "Light Turquoise"]
            [:option {:value "https://cdn.anychart.com/themes/latest/monochrome.min.js"} "Monochrome"]
            [:option {:value "https://cdn.anychart.com/themes/latest/morning.min.js"} "Morning"]
            [:option {:value "https://cdn.anychart.com/themes/latest/pastel.min.js"} "Pastel"]
            [:option {:value "https://cdn.anychart.com/themes/latest/sea.min.js"} "Sea"]
            [:option {:value "https://cdn.anychart.com/themes/latest/wines.min.js"} "Wines"]

            ]]

          [:div.form-group
           [:label {:for "settings-select-locale"} "Locales"]
           [:select.form-control {:id        "settings-select-locale"
                                  :on-change #(rf/dispatch [:settings/add-script (-> % .-target .-value)])
                                  :on-focus  #(set! (-> % .-target .-selectedIndex) -1)}
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/en-us.js"} "English"]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/de-de.js"} "German - Deutsch"]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/ru-ru.js"} "Russian - Русский "]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/es-es.js"} "Spanish - Español "]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/he-il.js"} "Israel - עברית"]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/zh-cn.js"} "Chinese - 中文 "]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/hi-in.js"} "India (Hindi) - हिंदी"]
            [:option {:value "https://cdn.anychart.com/locale/1.1.0/zh-hk.js"} "Chinese (Hong Kong) - 中文"]]]

          [:div.form-group
           [:label {:for "settings-select-map"} "Maps"]
           [:select.form-control {:id        "settings-select-map"
                                  :on-change #(rf/dispatch [:settings/add-script (-> % .-target .-value)])
                                  :on-focus  #(set! (-> % .-target .-selectedIndex) -1)}
            [:option {:value "https://cdn.anychart.com/geodata/1.2.0/custom/world/world.js"} "World"]
            [:option {:value "https://cdn.anychart.com/geodata/1.2.0/custom/world_source/world_source.js"} "World Origin"]
            [:option {:value "https://cdn.anychart.com/geodata/1.2.0/countries/australia/australia.topo.js"} "Australia"]
            [:option {:value "https://cdn.anychart.com/geodata/1.2.0/countries/united_states_of_america/united_states_of_america.topo.js"} "USA"]
            [:option {:value "https://cdn.anychart.com/geodata/1.2.0/countries/france/france.topo.js"} "France"]]]

          ])

       (when @(rf/subscribe [:settings/data-sets-tab?])
         [:div
          (for [data-set @(rf/subscribe [:data-sets])]
            ^{:key (:name data-set)}

            [:div.row.data-sets-item
             [:div.col-md-2.data-sets-item-icon
              [:img {:src (:logo data-set)}]]
             [:div.col-md-7
              [:h5 (:title data-set)]
              [:p (:description data-set)]
              (for [tag (:tags data-set)]
                ^{:key tag} [:span.label.label-primary.tag tag])]
             [:div.col-md-3
              [:a.btn.btn-primary.btn-xs.usage-sample-button {:href   (:sample data-set)
                                                              :target "_blank"
                                                              ;:on-click #(rf/dispatch [:settings/general-tab])
                                                              } "Usage Sample"]
              [:a.btn.btn-success.btn-xs.usage-sample-button {:href     "javascript:;"
                                                              :on-click #(do (utils/log (:type data-set))
                                                                             (case (:type data-set)
                                                                               "text/javascript" (rf/dispatch [:settings/add-script (:url data-set)])))}
               "Quick Add"]]])])

       [:button.btn.btn-default {:type     "button"
                                 :on-click #(rf/dispatch [:settings/hide])} "Close"]]

      ]]))
