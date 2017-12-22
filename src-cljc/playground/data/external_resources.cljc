(ns playground.data.external-resources
  (:require [clojure.string :as string]
            [camel-snake-kebab.core :as kebab])
  #?(:clj
     (:require [playground.data.external-resources-parser :as external-resources-parser])
     :cljs
     (:require-macros [playground.data.external-resources-parser :as external-resources-parser])))

;;======================================================================================================================
;; Main data
;;======================================================================================================================

(def data (external-resources-parser/parse-data-compile-time))

;;======================================================================================================================
;; Themes
;;======================================================================================================================
(defn compose-themes []
  (let [themes (:themes data)
        themes (map (fn [[url-name data]]
                      {:url         (str "https://cdn.anychart.com/releases/v8/themes/" (name url-name) ".js")
                       :name        (:name data)
                       :icon        (:icon data)
                       :description (:desc data)
                       :js          (str
                                      (string/lower-case (.substring (:name data) 0 1))
                                      (string/replace (.substring (:name data) 1) #" " ""))})
                    themes)
        themes (sort-by :name themes)]
    themes))


(def ^:const themes (compose-themes))

;;======================================================================================================================
;; Modules
;;======================================================================================================================
(defn compose-modules []
  (let [modules (remove (fn [[url-name data]] (:internal data)) (:modules data))
        modules (map (fn [[url-name data]]
                       {:name          (or (:name data) (str "Unnamed module with ID: " (name url-name)))
                        :description   (:desc data)
                        :url           (str "https://cdn.anychart.com/releases/v8/js/" (name url-name) ".min.js")
                        :example       "TODO: modules examples"
                        :internal-type (:type data)})
                     modules)
        modules (remove #(= (:internal-type %) "internal")
                        (sort-by :name modules))
        ;groups (group-by :internal-type modules)
        chart-types-modules (filter #(= "chart-type" (:internal-type %)) modules)
        feature-modules (filter #(= "feature" (:internal-type %)) modules)
        bundle-modules (filter #(= "bundle" (:internal-type %)) modules)
        misc-modules (filter #(and (not= "feature" (:internal-type %))
                                   (not= "chart-type" (:internal-type %))
                                   (not= "bundle" (:internal-type %)))
                             modules)]
    {:chart-types-modules chart-types-modules
     :features-modules    feature-modules
     :bundle-modules      bundle-modules
     :misc-moduls         misc-modules}))

(def ^:const all-modules (compose-modules))
(def ^:const chart-types-modules (:chart-types-modules all-modules))
(def ^:const feature-modules (:features-modules all-modules))
(def ^:const bundle-modules (:bundle-modules all-modules))
(def ^:const misc-modules (:misc-moduls all-modules))
(def ^:const binaries (flatten (map second all-modules)))

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


;(def ^:const locales
;  [{:url "https://cdn.anychart.com/releases/latest-v8/locales/en-us.js", :name "English" :js "en-us"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/de-de.js", :name "German - Deutsch" :js "de-de"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/ru-ru.js", :name "Russian - Русский" :js "ru-ru"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/es-es.js", :name "Spanish - Español" :js "es-es"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/he-il.js", :name "Israel - עברית" :js "he-il"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/zh-cn.js", :name "Chinese - 中文" :js "zh-cn"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/hi-in.js", :name "India (Hindi) - हिंदी" :js "hi-in"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/locales/zh-hk.js", :name "Chinese (Hong Kong) - 中文" :js "zh-hk"}])

;;======================================================================================================================
;; Locales
;;======================================================================================================================
(defn compose-locales [data]
  (let [locales (:locales data)
        locales (map (fn [[js-name item]]
                       (merge
                         item
                         {:name (str (:eng-name item) " - " (:native-name item))
                          :js   (name js-name)
                          :url  (str "https://cdn.anychart.com/releases/v8/locales/" (name js-name) ".js")}))
                     locales)]
    (sort-by :name locales)))

(def ^:const locales (compose-locales data))

;;======================================================================================================================
;; Maps
;;======================================================================================================================
;(def ^:const maps
;  [{:url "https://cdn.anychart.com/releases/latest-v8/geodata/custom/world/world.js", :name "World" :js "world"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/geodata/custom/world_source/world_source.js", :name "World Origin" :js "worldOrigin"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/geodata/countries/australia/australia.topo.js", :name "Australia" :js "australia"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/geodata/countries/united_states_of_america/united_states_of_america.topo.js", :name "USA" :js "usa"}
;   {:url "https://cdn.anychart.com/releases/latest-v8/geodata/countries/france/france.topo.js", :name "France" :js "france"}])

(defn compose-maps [data]
  (->> (:geodata data)
       (map
         (fn [[type-name type-items]]
           {:type  type-name
            :name  (string/replace (kebab/->HTTP-Header-Case (name type-name)) #"-" " ")
            :items (->> type-items
                        (map (fn [[js item]]
                               {:js   (name js)
                                :name (:name item)
                                :url  (str "https://cdn.anychart.com/releases/v8/geodata/"
                                           (name type-name) "/" (name js) "/" (name js) ".js")}))
                        (sort-by :name))}))
       (sort-by :name)))

(def ^:const maps-html (compose-maps data))
(def ^:const maps (mapcat :items maps-html))


;;======================================================================================================================
;; CSS
;;======================================================================================================================
(def ^:const css
  [{:url "https://cdn.anychart.com/releases/v8/css/anychart-ui.min.css", :name "AnyChart UI"}
   {:url "https://cdn.anychart.com/releases/v8/fonts/css/anychart-font.min.css", :name "AnyChart Font"}])


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
           :example "TODO: Add example to https://static.anychart.com/cdn/anydata/common/index.json?")) datasets))

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