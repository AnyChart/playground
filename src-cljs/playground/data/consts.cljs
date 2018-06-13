(ns playground.data.consts)


;; =====================================================================================================================
;; Tabs labels titles
;; =====================================================================================================================
(def ^:const scripts-title
  "Add any script, drag to change the order, click to edit the path.")

(def ^:const styles-title
  "Add any CSS, drag to change the order, click to edit the path.")


;; =====================================================================================================================
;; Question mark titles
;; =====================================================================================================================
(def ^:const anychart-binaries-title
  (str "AnyChart Library has a lot of different modules, choose\n"
       "only those you need to speed up the loading time."))


(def ^:const anychart-locales-title
  (str "AnyChart Locales files are needed when you want to use\n"
       "AnyChart localization options."))


(def ^:const anychart-themes-title
  (str "AnyChart Themes is a set of ready to use presets of\n"
       "visual settings."))


(def ^:const anychart-geo-title
  (str "A AnyChart GEO Data are files required by AnyMap module\n"
       "to display the geographical maps."))


(def ^:const anychart-css
  (str "AnyChart CSS file is required for AnyChart UI components,\n"
       "like context menu or zoom buttons, to work properly."))


;; =====================================================================================================================
;; Warnings
;; =====================================================================================================================
(def ^:const script-style-warning
  (str "This AnyChart module has different version and this conflict may lead to fatal errors. "
       "Fix this or proceed at your own risk."))


(def ^:const script-order-warning
  (str "This AnyChart module must be after main AnyChart Bundle or AnyChart Base module. "
       "Fix this or proceed at your own risk."))


(def ^:const settings-warning
  "One or several AnyChart modules have conflicting versions or bad positions, it is recommended that you fix this issue.")


(def ^:const modal-window-warning
  "One or several AnyChart modules have conflicting versions or bad positions, it is recommended that you fix this issue. Are you sure you want to proceed?")