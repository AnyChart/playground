(ns playground.settings-window.external-resources.views
  (:require [re-frame.core :as rf]))


(defn version-select []
  [:div
   [:div.form-group
    [:label {:for "settings-select-version"} "AnyChart version"]
    [:select.form-control {:id            "settings-select-version"
                           :default-value @(rf/subscribe [:settings.external-resources/selected-version])
                           :on-change     #(rf/dispatch [:settings.external-resources/change-version (-> % .-target .-value)])}


     (for [v @(rf/subscribe [:settings/versions-names])]
       ^{:key (str "v" v)}
       [:option {:value v} v])
     ]]])
