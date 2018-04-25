(ns playground.tips.tips-data)

;;======================================================================================================================
;; Composition for tips
;;======================================================================================================================
(defn get-binaries [binaries]
  (map (fn [{:keys [url name] :as item}]
         (assoc item
           :title name
           :type :binary))
       binaries))


(defn get-themes [themes]
  (map (fn [{:keys [url name js] :as item}]
         (assoc item
           :title "Themes"
           :description "TODO: locales description"
           :example (str "anychart.theme(\"" js "\");\nchart = anychart.pie();\nchart.data([10, 20, 18, 60, 28]);\nchart.container(\"container\");\nchart.draw();")
           :type :theme))
       themes))


(defn get-locales [locales]
  (map (fn [{:keys [url name js] :as item}]
         (assoc item
           :title "Locales"
           :description "TODO: themes description"
           :example (str "anychart.format.outputLocale(\"" js "\");\nanychart.format.outputDateTimeFormat(\"dd MMMM yyyy\");\n\nchart = anychart.line([\n  [Date.UTC(2014, 5, 6), 511.53, 500.98, 680.79, 656.40],\n  [Date.UTC(2014, 6, 8), 900, 850, 1100, 1050],\n  [Date.UTC(2014, 9, 10), 700, 630.40, 810.58, 790],\n  [Date.UTC(2014, 10, 12), 380, 315, 450, 415],\n  [Date.UTC(2014, 12, 14), 830, 513, 405, 145]\n]);\n\nvar axis = chart.xAxis();\nvar formatLabels = axis.labels();\nformatLabels.format(function() {\n  return anychart.format.dateTime(this.value);\n});\n\nchart.container(\"container\");\nchart.draw();   ")
           :type :locale))
       locales))


(defn get-maps [maps]
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


(defn get-csss [css]
  (map (fn [{:keys [url name] :as item}]
         (assoc item
           :title "CSS"
           :description "TODO: css description"
           :example (str "some code\nis needed here")
           :type :css))
       css))


(defn compose-all-data [binaries themes locales maps csss datasets]
  (concat
    (get-binaries binaries)
    (get-themes themes)
    (get-locales locales)
    (get-maps maps)
    (get-csss csss)
    (get-datasets datasets)))


(defn get-tip [url db]
  (first (filter #(= url (:url %)) (-> db :tips :data))))