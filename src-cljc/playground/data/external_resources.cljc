(ns playground.data.external-resources
  (:require [clojure.string :as string])
  #?(:clj
     (:require [playground.data.external-resources-parser :as external-resources-parser])
     :cljs
     (:require-macros [playground.data.external-resources-parser :as external-resources-parser])))

;;======================================================================================================================
;; Main data
;;======================================================================================================================

(def data (external-resources-parser/parse-data-compile-time))

(defn compose-themes []
  (let [themes (:themes data)
        themes (map (fn [[url-name data]]
                      {:url         (str "http://cdn.anychart.com/releases/latest-v8/themes/" (name url-name) ".js")
                       :name        (:name data)
                       :icon        (:icon data)
                       :description (:desc data)
                       :js          (str
                                      (string/lower-case (.substring (:name data) 0 1))
                                      (string/replace (.substring (:name data) 1) #" " ""))})
                    themes)]
    themes))


(def ^:const themes (compose-themes))

(defn compose-modules []
  (let [modules (remove (fn [[url-name data]] (:internal data)) (:modules data))
        modules (map (fn [[url-name data]]
                       {:name        (or (:name data) (str "Unnamed module with ID: " (name url-name)))
                        :description (:desc data)
                        :url         (str "http://cdn.anychart.com/releases/latest-v8/js/" (name url-name) ".min.js")
                        :example     "TODO: modules examples"})
                     modules)]
    modules))

(def ^:const binaries (compose-modules))

;(def ^:const binaries
;  [{:url         "https://cdn.anychart.com/js/latest/anychart-bundle.min.js"
;    :name        "AnyChart"
;    :description "AnyChart is a flexible JavaScript (HTML5) based charting solution which will fit any need for data visualization."
;    :example     "anychart.onDocumentLoad(function() {\n    // create an instance of pie chart with data\n    var chart = anychart.pie([\n        [\"Chocolate\", 5],\n        [\"Rhubarb compote\", 2],\n        [\"Crêpe Suzette\", 2],\n        [\"American blueberry\", 2],\n        [\"Buttermilk\", 1]\n    ]);\n    chart.title(\"Top 5 pancake fillings\");\n    // pass the container where chart will be drawn\n    chart.container(\"container\");\n    // call the chart draw() method to initiate chart drawing\n    chart.draw();\n});"}
;
;   {:url         "https://cdn.anychart.com/js/latest/data-adapter.min.js"
;    :name        "Data Adapter"
;    :description "TODO: Data Adapter description"
;    :example     "anychart.data.loadJsonFile(\"https://cdn.anychart.com/charts-data/data_json.json\", function (data) {\n  chart = anychart.column();\n  chart.data(data);\n  chart.container(\"container\");\n  chart.draw();\n});"}])


;(def ^:const themes
;  [{:url "https://cdn.anychart.com/themes/latest/coffee.min.js", :name "Coffee" :js "coffee"}
;   {:url "https://cdn.anychart.com/themes/latest/dark_blue.min.js", :name "Dark Blue" :js "darkBlue"}
;   {:url "https://cdn.anychart.com/themes/latest/dark_earth.min.js", :name "Dark Earth" :js "darkEarth"}
;   {:url "https://cdn.anychart.com/themes/latest/dark_glamour.min.js", :name "Dark Glamour" :js "darkGlamour"}
;   {:url "https://cdn.anychart.com/themes/latest/dark_provence.min.js", :name "Dark Provence" :js "darkTurquoise"}
;   {:url "https://cdn.anychart.com/themes/latest/dark_turquoise.min.js", :name "Dark Turquoise" :js "darkTurquoise"}
;   {:url "https://cdn.anychart.com/themes/latest/defaultTheme.min.js", :name "Default Theme" :js "defaultTheme"}
;   {:url "https://cdn.anychart.com/themes/latest/light_blue.min.js", :name "Light Blue" :js "lightBlue"}
;   {:url "https://cdn.anychart.com/themes/latest/light_earth.min.js", :name "Light Earth" :js "lightEarth"}
;   {:url "https://cdn.anychart.com/themes/latest/light_glamour.min.js", :name "Light Glamour" :js "lightGlamour"}
;   {:url "https://cdn.anychart.com/themes/latest/light_provence.min.js", :name "Light Provence" :js "lightProvence"}
;   {:url "https://cdn.anychart.com/themes/latest/light_turquoise.min.js", :name "Light Turquoise" :js "lightTurquoise"}
;   {:url "https://cdn.anychart.com/themes/latest/monochrome.min.js", :name "Monochrome" :js "monochrome"}
;   {:url "https://cdn.anychart.com/themes/latest/morning.min.js", :name "Morning" :js "morning"}
;   {:url "https://cdn.anychart.com/themes/latest/pastel.min.js", :name "Pastel" :js "pastel"}
;   {:url "https://cdn.anychart.com/themes/latest/sea.min.js", :name "Sea" :js "sea"}
;   {:url "https://cdn.anychart.com/themes/latest/wines.min.js", :name "Wines" :js "wines"}])


(def ^:const locales
  [{:url "https://cdn.anychart.com/locale/1.1.0/en-us.js", :name "English" :js "en-us"}
   {:url "https://cdn.anychart.com/locale/1.1.0/de-de.js", :name "German - Deutsch" :js "de-de"}
   {:url "https://cdn.anychart.com/locale/1.1.0/ru-ru.js", :name "Russian - Русский" :js "ru-ru"}
   {:url "https://cdn.anychart.com/locale/1.1.0/es-es.js", :name "Spanish - Español" :js "es-es"}
   {:url "https://cdn.anychart.com/locale/1.1.0/he-il.js", :name "Israel - עברית" :js "he-il"}
   {:url "https://cdn.anychart.com/locale/1.1.0/zh-cn.js", :name "Chinese - 中文" :js "zh-cn"}
   {:url "https://cdn.anychart.com/locale/1.1.0/hi-in.js", :name "India (Hindi) - हिंदी" :js "hi-in"}
   {:url "https://cdn.anychart.com/locale/1.1.0/zh-hk.js", :name "Chinese (Hong Kong) - 中文" :js "zh-hk"}])


(def ^:const maps
  [{:url "https://cdn.anychart.com/geodata/1.2.0/custom/world/world.js", :name "World" :js "world"}
   {:url "https://cdn.anychart.com/geodata/1.2.0/custom/world_source/world_source.js", :name "World Origin" :js "worldOrigin"}
   {:url "https://cdn.anychart.com/geodata/1.2.0/countries/australia/australia.topo.js", :name "Australia" :js "australia"}
   {:url "https://cdn.anychart.com/geodata/1.2.0/countries/united_states_of_america/united_states_of_america.topo.js", :name "USA" :js "usa"}
   {:url "https://cdn.anychart.com/geodata/1.2.0/countries/france/france.topo.js", :name "France" :js "france"}])

(def ^:const css
  [{:url "https://cdn.anychart.com/releases/latest-v8/css/anychart-ui.min.css", :name "AnyChart UI"}
   {:url "https://cdn.anychart.com/releases/latest-v8/fonts/css/anychart-font.min.css", :name "AnyChart Font"}])


;;======================================================================================================================
;; Getters
;;======================================================================================================================
(defn get-binary-by-url [url]
  (first (filter #(= url (:url %)) binaries)))


(defn get-theme-by-url [url]
  (first (filter #(= url (:url %)) themes)))


(defn get-locale-by-url [url]
  (first (filter #(= url (:url %)) locales)))


(defn get-map-by-url [url]
  (first (filter #(= url (:url %)) maps)))


(defn get-css-by-url [url]
  (first (filter #(= url (:url %)) css)))

;;======================================================================================================================
;; Composition for tips
;;======================================================================================================================
(defn get-binaries []
  (map (fn [{:keys [url name] :as item}]
         (assoc item
           :title name
           :type :binary))
       binaries))


(defn get-themes []
  (map (fn [{:keys [url name js] :as item}]
         (assoc item
           :title "Themes"
           :description "TODO: locales description"
           :example (str "anychart.theme(\"" js "\");\nchart = anychart.pie();\nchart.data([10, 20, 18, 60, 28]);\nchart.container(\"container\");\nchart.draw();")
           :type :theme))
       themes))


(defn get-locales []
  (map (fn [{:keys [url name js] :as item}]
         (assoc item
           :title "Locales"
           :description "TODO: themes description"
           :example (str "anychart.format.outputLocale(\"" js "\");\nanychart.format.outputDateTimeFormat(\"dd MMMM yyyy\");\n\nchart = anychart.line([\n  [Date.UTC(2014, 5, 6), 511.53, 500.98, 680.79, 656.40],\n  [Date.UTC(2014, 6, 8), 900, 850, 1100, 1050],\n  [Date.UTC(2014, 9, 10), 700, 630.40, 810.58, 790],\n  [Date.UTC(2014, 10, 12), 380, 315, 450, 415],\n  [Date.UTC(2014, 12, 14), 830, 513, 405, 145]\n]);\n\nvar axis = chart.xAxis();\nvar formatLabels = axis.labels();\nformatLabels.format(function() {\n  return anychart.format.dateTime(this.value);\n});\n\nchart.container(\"container\");\nchart.draw();   ")
           :type :locale))
       locales))


(defn get-maps []
  (map (fn [{:keys [url name js] :as item}]
         (assoc item
           :title "Maps"
           :description "TODO: maps description"
           :example (str "australiaMap = anychart.map();\naustraliaMap.geoData(anychart.maps." js ");\naustraliaMap.container(\"container\");\naustraliaMap.draw();  ")
           :type :map))
       maps))


(defn get-datasets [datasets]
  (map (fn [item]
         (assoc item
           :type :dataset
           :example "TODO: Add example to http://static.anychart.com/cdn/anydata/common/index.json?")) datasets))

(defn get-csss []
  (map (fn [{:keys [url name] :as item}]
         (assoc item
           :title "CSS"
           :description "TODO: css description"
           :example (str "some code\nis needed here")
           :type :css))
       css))

(defn compose-all-data [datasets]
  (concat
    (get-binaries)
    (get-themes)
    (get-locales)
    (get-maps)
    (get-csss)
    (get-datasets datasets)))


(defn get-tip [url all-data]
  (first (filter #(= url (:url %)) all-data)))