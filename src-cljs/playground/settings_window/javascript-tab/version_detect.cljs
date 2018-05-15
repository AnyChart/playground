(ns playground.settings-window.javascript-tab.version-detect
  (:require [clojure.string :as string]))


(defn url-to-version [url]
  (case url
    "v8" "latest"
    url))


(defn version-to-url [version]
  (case version
    "latest" "v8"
    version))


(defn script-version [script]
  (last (re-find #"^https?://cdn.anychart.com/(releases|js|css|themes)/([^/]+)/.*$" script)))


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


(defn replace-version [version script]
  (let [version (version-to-url version)]
    (string/replace script #"^https?://cdn.anychart.com/(releases|js|css|themes)/([^/]+)/.*$"
                    (fn [[s type old-version]]
                      (string/replace-first s (re-pattern old-version) version)))))


(defn replace-version-scripts [version scripts]
  (map #(replace-version version %) scripts))