(ns playground.settings-window.javascript-tab.version-detect
  (:require [clojure.string :as string]))


(defn url-to-version [url]
  (case url
    "v8" "latest"
    url))


(defn script-version [script]
  (cond
    (string/starts-with? script "https://cdn.anychart.com/releases/")
    (second (re-find #"https://cdn.anychart.com/releases/([^/]+)/.*" script))

    (string/starts-with? script "https://cdn.anychart.com/js/")
    (second (re-find #"https://cdn.anychart.com/js/([^/]+)/.*" script))

    (string/starts-with? script "https://cdn.anychart.com/themes/")
    (second (re-find #"https://cdn.anychart.com/themes/([^/]+)/.*" script))

    (string/starts-with? script "https://cdn.anychart.com/css/")
    (second (re-find #"https://cdn.anychart.com/css/([^/]+)/.*" script))
    :else nil))


(defn detect-version [scripts]
  (let [bundles (filter (fn [script]
                          (or
                            (string/includes? script "anychart-bundle.min.js")
                            (string/includes? script "anychart-base.min.js")
                            (string/includes? script "anychart.min.js")
                            (string/includes? script "anystock.min.js")
                            (string/includes? script "anygantt.min.js")
                            (string/includes? script "anymap.min.js")
                            (string/includes? script "graphics.min.js")))
                        scripts)
        bundle (first bundles)
        v (when bundle
            (script-version bundle))]
    (url-to-version v)))


(defn correct-script? [script detected-version]
  (let [current-version (url-to-version (script-version script))]
    (or (nil? current-version) (= current-version detected-version))))


(defn to-correct-scripts [scripts detected-version]
  (map (fn [script]
         {:correct (correct-script? script detected-version)
          :script  script})
       scripts))


(defn to-correct-styles [styles detected-version]
  (map (fn [style]
         {:correct (correct-script? style detected-version)
          :style   style})
       styles))