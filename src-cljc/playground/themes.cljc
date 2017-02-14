(ns playground.themes
  (:require [version-clj.core :as v :refer [version-compare]]))

(def raw-default-theme {:name "Default Theme" :script "defaultTheme" :js "defaultTheme"})

(def all-raw-themes [raw-default-theme
                     {:name "Coffee" :script "coffee" :js "coffee"}
                     {:name "Dark Blue" :script "dark_blue" :js "darkBlue"}
                     {:name "Dark Earth" :script "dark_earth" :js "darkEarth"}
                     {:name "Dark Glamour" :script "dark_glamour" :js "darkGlamour"}
                     {:name "Dark Provence" :script "dark_provence" :js "darkProvence"}
                     {:name "Dark Turquoise" :script "dark_turquoise" :js "darkTurquoise"}
                     {:name "Light Blue" :script "light_blue" :js "lightBlue"}
                     {:name "Light Earth" :script "light_earth" :js "lightEarth"}
                     {:name "Light Glamour" :script "light_glamour" :js "lightGlamour"}
                     {:name "Light Provence" :script "light_provence" :js "lightProvence"}
                     {:name "Light Turquoise" :script "light_turquoise" :js "lightTurquoise"}
                     {:name "Monochrome" :script "monochrome" :js "monochrome"}
                     {:name "Morning" :script "morning" :js "morning"}
                     {:name "Pastel" :script "pastel" :js "pastel"}
                     {:name "Sea" :script "sea" :js "sea"}
                     {:name "6.x Version Theme" :script "v6" :js "v6"}
                     {:name "Wines" :script "wines" :js "wines"}])

(def two-raw-themes [raw-default-theme
                     {:name "6.x Version Theme" :script "v6" :js "v6"}])

(def themes {"7.10.1" all-raw-themes
             "7.10.0" all-raw-themes
             "7.9.1"  all-raw-themes
             "7.9.0"  two-raw-themes
             "7.8.0"  two-raw-themes
             "7.7.0"  two-raw-themes
             "7.6.0"  two-raw-themes})

(defn- get-script-url [version-key theme]
  (str "http://cdn.anychart.com/themes/" version-key "/" (:script theme) ".min.js"))

(defn- get-full-themes [version themes]
  (map #(assoc % :script (get-script-url version %)) themes))

(def last-version-themes (get themes (last (sort version-compare (keys themes)))))

(defn get-default-theme
  ([] (get-full-themes "latest" [raw-default-theme]))
  ([version-key] (if (re-matches #"^\d+\.\d+\.\d+$" version-key)
                   (get-full-themes version-key [raw-default-theme])
                   (get-default-theme))))

(defn get-themes
  ([] (get-full-themes "latest" all-raw-themes))
  ([version-key]
   (if (re-matches #"^\d+\.\d+\.\d+$" version-key)
     (if (= 1 (version-compare version-key "7.10.1"))
      (get-full-themes version-key all-raw-themes)
      (get-full-themes version-key (get themes version-key)))
     (get-themes))))