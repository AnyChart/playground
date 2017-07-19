(ns playground.settings-window.data)


(def ^:const binaries
  [{:link "https://cdn.anychart.com/js/latest/anychart-bundle.min.js" :name "AnyChart"}
   {:link "https://cdn.anychart.com/js/latest/data-adapter.min.js" :name "Data Adapter"}])


(def ^:const themes
  [{:link "https://cdn.anychart.com/themes/latest/coffee.min.js", :name "Coffee"}
   {:link "https://cdn.anychart.com/themes/latest/dark_blue.min.js", :name "Dark Blue"}
   {:link "https://cdn.anychart.com/themes/latest/dark_earth.min.js", :name "Dark Earth"}
   {:link "https://cdn.anychart.com/themes/latest/dark_glamour.min.js", :name "Dark Glamour"}
   {:link "https://cdn.anychart.com/themes/latest/dark_provence.min.js", :name "Dark Provence"}
   {:link "https://cdn.anychart.com/themes/latest/dark_turquoise.min.js", :name "Dark Turquoise"}
   {:link "https://cdn.anychart.com/themes/latest/defaultTheme.min.js", :name "Default Theme"}
   {:link "https://cdn.anychart.com/themes/latest/light_blue.min.js", :name "Light Blue"}
   {:link "https://cdn.anychart.com/themes/latest/light_earth.min.js", :name "Light Earth"}
   {:link "https://cdn.anychart.com/themes/latest/light_glamour.min.js", :name "Light Glamour"}
   {:link "https://cdn.anychart.com/themes/latest/light_provence.min.js", :name "Light Provence"}
   {:link "https://cdn.anychart.com/themes/latest/light_turquoise.min.js", :name "Light Turquoise"}
   {:link "https://cdn.anychart.com/themes/latest/monochrome.min.js", :name "Monochrome"}
   {:link "https://cdn.anychart.com/themes/latest/morning.min.js", :name "Morning"}
   {:link "https://cdn.anychart.com/themes/latest/pastel.min.js", :name "Pastel"}
   {:link "https://cdn.anychart.com/themes/latest/sea.min.js", :name "Sea"}
   {:link "https://cdn.anychart.com/themes/latest/wines.min.js", :name "Wines"}])


(def ^:const locales
  [{:link "https://cdn.anychart.com/locale/1.1.0/en-us.js", :name "English"}
   {:link "https://cdn.anychart.com/locale/1.1.0/de-de.js", :name "German - Deutsch"}
   {:link "https://cdn.anychart.com/locale/1.1.0/ru-ru.js", :name "Russian - Русский "}
   {:link "https://cdn.anychart.com/locale/1.1.0/es-es.js", :name "Spanish - Español "}
   {:link "https://cdn.anychart.com/locale/1.1.0/he-il.js", :name "Israel - עברית"}
   {:link "https://cdn.anychart.com/locale/1.1.0/zh-cn.js", :name "Chinese - 中文 "}
   {:link "https://cdn.anychart.com/locale/1.1.0/hi-in.js", :name "India (Hindi) - हिंदी"}
   {:link "https://cdn.anychart.com/locale/1.1.0/zh-hk.js", :name "Chinese (Hong Kong) - 中文"}])


(def ^:const maps
  [{:link "https://cdn.anychart.com/geodata/1.2.0/custom/world/world.js", :name "World"}
   {:link "https://cdn.anychart.com/geodata/1.2.0/custom/world_source/world_source.js", :name "World Origin"}
   {:link "https://cdn.anychart.com/geodata/1.2.0/countries/australia/australia.topo.js", :name "Australia"}
   {:link "https://cdn.anychart.com/geodata/1.2.0/countries/united_states_of_america/united_states_of_america.topo.js",
    :name "USA"}
   {:link "https://cdn.anychart.com/geodata/1.2.0/countries/france/france.topo.js", :name "France"}])


(defn get-binary-by-url [url]
  (first (filter #(= url (:link %)) binaries)))


(defn get-theme-by-url [url]
  (first (filter #(= url (:link %)) themes)))


(defn get-locale-by-url [url]
  (first (filter #(= url (:link %)) locales)))


(defn get-map-by-url [url]
  (first (filter #(= url (:link %)) maps)))