(ns playground.settings-window.external-resources.parser
  (:require [clojure.string :as string]
            [camel-snake-kebab.core :as kebab]))


(defn get-version-url [version]
  (case version
    "latest" "v8"
    version))


(defn get-modules-url [version-url]
  (str "http://cdn.anychart.com/releases/" version-url "/js/modules.json"))


(defn v8? [version]
  (string/starts-with? version "8."))


(defn v7? [version]
  (string/starts-with? version "7."))


(defn compose-themes [data version]
  (let [themes (:themes data)
        themes (map (fn [[url-name data]]
                      {:url         (cond
                                      (v7? version) (str "https://cdn.anychart.com/themes/" version "/" (name url-name) ".js")
                                      :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/themes/" (name url-name) ".js"))
                       :name        (:name data)
                       :icon        (:icon data)
                       :description (:desc data)
                       :js          (str
                                      (string/lower-case (subs (:name data) 0 1))
                                      (string/replace (subs (:name data) 1) #" " ""))})
                    themes)
        themes (sort-by :name themes)]
    themes))


(defn compose-locales [data version]
  (let [locales (:locales data)
        locales (map (fn [[js-name item]]
                       (merge
                         item
                         {:name (str (:eng-name item) " - " (:native-name item))
                          :js   (name js-name)
                          :url  (cond
                                  (v7? version) (str "https://cdn.anychart.com/locale/1.0.0/" (name js-name) ".js")
                                  :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/locales/" (name js-name) ".js"))}))
                     locales)]
    (sort-by :name locales)))


(defn compose-maps [data version]
  (let [geodata (:geodata data)
        groups (map
                 (fn [[type-name type-items]]
                   {:type  type-name
                    :name  (string/replace (kebab/->HTTP-Header-Case (name type-name)) #"-" " ")
                    :items (->> type-items
                                (map (fn [[js item]]
                                       {:js   (name js)
                                        :name (:name item)
                                        :url  (cond
                                                (v7? version) (str "https://cdn.anychart.com/geodata/1.2.0/"
                                                                   (name type-name) "/" (name js) "/" (name js) ".js")
                                                :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/geodata/"
                                                           (name type-name) "/" (name js) "/" (name js) ".js"))}))
                                (sort-by :name))})
                 geodata)
        groups (sort-by :name groups)]
    {:maps        (mapcat :items groups)
     :maps-groups groups}))


(defn compose-modules [data version]
  (let [modules (remove (fn [[url-name data]] (:internal data)) (:modules data))
        modules (map (fn [[url-name data]]
                       {:name          (or (:name data) (str "Unnamed module with ID: " (name url-name)))
                        :description   (:desc data)
                        :url           (cond
                                         (v7? version) (str "https://cdn.anychart.com/js/" version "/" (name url-name) ".min.js")
                                         :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/js/" (name url-name) ".min.js"))
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
     :misc-modules        misc-modules
     :binaries-groups     [{:name  "Chart Types"
                            :type  :chart-type
                            :items chart-types-modules}
                           {:name  "Features"
                            :type  :feature
                            :items feature-modules}
                           {:name  "Bundles"
                            :type  :bundle
                            :items bundle-modules}
                           {:name  "Misc"
                            :type  :misc
                            :items misc-modules}]
     :binaries            (concat chart-types-modules feature-modules bundle-modules misc-modules)}))


(defn compose-css [version]
  (let [css [{:url  (cond
                      (v7? version) (str "https://cdn.anychart.com/css/" version "/anychart-ui.min.css")
                      :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/css/anychart-ui.min.css"))
              :name "AnyChart UI"}
             {:url  (cond
                      (v7? version) (str "https://cdn.anychart.com/css/" version "/anychart-font.min.css")
                      :else (str "https://cdn.anychart.com/releases/" (get-version-url version) "/fonts/css/anychart-font.min.css"))
              :name "AnyChart Font"}]]
    css))


(defn data [data version]
  {:themes  (compose-themes data version)
   :locales (compose-locales data version)
   :geodata (compose-maps data version)
   :modules (compose-modules data version)
   :css     (compose-css version)})
