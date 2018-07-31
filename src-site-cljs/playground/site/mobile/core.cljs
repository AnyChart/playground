(ns playground.site.mobile.core
  (:require [goog.dom :as dom]
            [goog.style :as style]
            [goog.events :as event]))


(def show (atom false))


(defn show-leftmenu []
  (let [menu (dom/getElement "leftmenu")]
    (style/setStyle menu "visibility" "visible")))


(defn hide-leftmenu []
  (let [menu (dom/getElement "leftmenu")]
    (style/setStyle menu "visibility" "hidden")))


(defn toggle-menu []
  (if @show
    (hide-leftmenu)
    (show-leftmenu))
  (swap! show update not))


(defn init []
 (let [switcher (dom/getElement "bars-switcher")
       close-button (dom/getElement "leftmenu-close")]
   (event/listen switcher "click" #(show-leftmenu))
   (event/listen close-button "click" #(hide-leftmenu))))


(init)