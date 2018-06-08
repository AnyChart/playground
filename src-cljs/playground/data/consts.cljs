(ns playground.data.consts)


(def ^:const scripts-title
  "Add any script, drag to change the order, click to edit the path.")


(def ^:const anychart-binaries-title
  "AnyChart Library has a lot of different modules, choose only those you need to speed up the loading time.")


(def ^:const anychart-locales-title
  "AnyChart Locales files are needed when you want to use AnyChart localization options.")


(def ^:const anychart-themes-title
  "AnyChart Themes is a set of ready to use presets of visual settings.")


(def ^:const anychart-geo-title
  "A AnyChart GEO Data are files required by AnyMap module to display the geographical maps.")


(def ^:const styles-title
  "Add any CSS, drag to change the order, click to edit the path.")


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