(ns playground.site.mobile.core
  (:require [goog.dom :as dom]
            [goog.style :as style]
            [goog.events :as event]))


(def show (atom false))


(defn show-leftmenu []
  (let [menu (dom/getElement "leftmenu")
        menu-bg (dom/getElement "leftmenu-bg")]
    (style/setStyle menu "visibility" "visible")
    (style/setStyle menu-bg "visibility" "visible")))


(defn hide-leftmenu []
  (let [menu (dom/getElement "leftmenu")
        menu-bg (dom/getElement "leftmenu-bg")]
    (style/setStyle menu "visibility" "hidden")
    (style/setStyle menu-bg "visibility" "hidden")))


(defn toggle-menu []
  (if @show
    (hide-leftmenu)
    (show-leftmenu))
  (swap! show update not))


(defn init []
 (let [switcher (dom/getElement "bars-switcher")
       close-button (dom/getElement "leftmenu-close")
       menu-bg (dom/getElement "leftmenu-bg")]
   (event/listen switcher "click" #(show-leftmenu))
   (event/listen close-button "click" #(hide-leftmenu))
   (event/listen menu-bg "click" #(hide-leftmenu))))


(init)