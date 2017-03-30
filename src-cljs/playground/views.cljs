(ns playground.views
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]))


;; -- View Functions ----------------------------------------------
(defn navbar []
  [:nav {:class "navbar navbar-default"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:button {:type          "button"
               :class         "navbar-toggle collapsed"
               :data-toggle   "collapse"
               :data-target   "#navbar"
               :aria-expanded "false"
               :aria-controls "navbar"}
      [:span {:class "sr-only"} "Toggle navigation"]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]]
     [:a {:class "navbar-brand" :href "//anychart.com/"}
      [:img {:src    "/icons/anychart.png"
             :style  {:display "inline-block"}
             :width  "30"
             :height "30"
             :alt    "AnyChart"}]
      "AnyChart Playground"]]
    [:div {:id "navbar" :class "navbar-collapse collapse"}
     [:ul {:class "nav navbar-nav"}
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:run])} "Run"]]
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:save])} "Save"]]
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:fork])} "Fork"]]
      [:li [:a {:href     "javascript:;"
                :on-click #(rf/dispatch [:show-settings])} "Settings"]]
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "View"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        [:li [:a {:href "#"} "Editor"]]
        [:li [:a {:href "#"} "Standalone"]]
        [:li [:a {:href "#"} "Iframe"]]]]]
     [:ul {:class "nav navbar-nav navbar-right"}
      [:li {:class "dropdown"}
       [:a {:href          "#"
            :class         "dropdown-toggle"
            :data-toggle   "dropdown"
            :role          "button"
            :aria-haspopup "true"
            :aria-expanded "false"} "New Chart"
        [:span {:class "caret"}]]
       [:ul {:class "dropdown-menu"}
        [:li [:a {:href "#"} "Chart"]]
        [:li [:a {:href "#"} "Stock"]]
        [:li [:a {:href "#"} "Gantt"]]
        [:li [:a {:href "#"} "Map"]]
        [:li {:role "separator" :class "divider"}]
        [:li [:a {:href "#"} "From scratch"]]]]
      [:li [:a {:href "/signin"} "Log In"]]
      [:li [:a {:href "/signup"} "Sign Up"]]]]]])

(defn footer []
  )

(defn editors []
  [:div.column-container {:style {:height @(rf/subscribe [:editors-height])}}
   [:fieldset.column-left
    [:div.editor-box
     [:div#markup-editor]]
    [:div.editor-box
     [:div#style-editor]]
    [:div.editor-box
     [:div#code-editor]]]
   [:fieldset.column-right
    [:div.result
     [:iframe#result-iframe {:name              "result-iframe"
                             :class             "iframe-result"
                             :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"
                             :allowTransparency "true"
                             :allowFullScreen   "true"
                             :src               @(rf/subscribe [:sample-iframe-url])}]]]])

(defn send-form []
  [:form#run-form
   {:style  {:display "none"}
    :action "/run"
    :target "result-iframe"
    :method "POST"}
   [:input {:name  "code"
            :value @(rf/subscribe [:code])
            :type  "hidden"}]
   [:input {:name  "markup"
            :value @(rf/subscribe [:markup])
            :type  "hidden"}]
   [:input {:name  "style"
            :value @(rf/subscribe [:style])
            :type  "hidden"}]
   [:input {:name  "styles"
            :value @(rf/subscribe [:styles])
            :type  "hidden"}]
   [:input {:name  "scripts"
            :value @(rf/subscribe [:scripts])
            :type  "hidden"}]])

(defn settings-window []
  [:div.settings-window
   [:div.settings-window-background {:on-click #(rf/dispatch [:hide-settings])}]
   [:div.settings-window-container
    [:form
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
      [:input.form-control {:id    "settings-tags"
                            :value @(rf/subscribe [:tags])}]]

     [:div.form-group
      [:label {:for "settings-scripts"} "Scripts"]
      [:textarea.form-control {:id    "settings-scripts"
                               :value @(rf/subscribe [:scripts])}]]

     [:div.form-group
      [:label {:for "settings-styles"} "Styles"]
      [:textarea.form-control {:id    "settings-styles"
                               :value @(rf/subscribe [:styles])}]]
     [:div.form-inline
      {:style {:padding-right "10px"}}

      [:div.form-group
       [:label {:for "settings-markup-type"} "Markup type"]
       [:select.form-control {:id            "settings-markupt-type"
                              :default-value @(rf/subscribe [:markup-type])}
        [:option "HTML"]
        [:option "Slim"]
        [:option "Pug"]]]

      [:div.form-group
       [:label {:for "settings-style-type"} "Style type"]
       [:select.form-control {:id            "settings-style-type"
                              :default-value @(rf/subscribe [:style-type])}
        [:option "CSS"]
        [:option "Sass"]
        [:option "LESS"]]]

      [:div.form-group
       [:label {:for "settings-code-type"} "Code type"]
       [:select.form-control {:id            "settings-code-type"
                              :default-value @(rf/subscribe [:code-type])}
        [:option "JavaScript"]
        [:option "CoffeeScript"]
        [:option "TypeScript"]]]]
     [:button {:type     "button"
               :on-click #(rf/dispatch [:hide-settings])} "Close"]]]])

(defn app []
  [:div
   [send-form]
   [navbar]
   [editors]
   (when @(rf/subscribe [:settings-show])
     [settings-window])])
