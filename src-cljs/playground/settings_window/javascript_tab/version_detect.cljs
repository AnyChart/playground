(ns playground.settings-window.javascript-tab.version-detect
  (:require [clojure.string :as string]
            [playground.data.consts :as consts]))


(defn url-to-version [url]
  (case url
    "v8" "latest"
    "rc" "Release Candidate"
    url))


(defn version-to-url [version]
  (case version
    "latest" "v8"
    "Release Candidate" "rc"
    version))


(defn script-version [script]
  (last (re-find #"^https?://cdn.anychart.com/(releases|js|css|themes)/([^/]+)/.*$" script)))


(defn detect-version [scripts]
  (let [bundles (keep-indexed (fn [idx script]
                                (when (or
                                        (string/includes? script "anychart-bundle.min.js")
                                        (string/includes? script "anychart-base.min.js")
                                        (string/includes? script "anychart-core.min.js")
                                        (string/includes? script "anychart.min.js")
                                        (string/includes? script "anystock.min.js")
                                        (string/includes? script "anygantt.min.js")
                                        (string/includes? script "anymap.min.js")
                                        (string/includes? script "graphics.min.js"))
                                  {:index idx :script script}))
                              scripts)
        result (first bundles)]
    (when result
      {:index   (:index result)
       :version (url-to-version (script-version (:script result)))})))


(defn correct-script? [script detected-version]
  (let [current-version (url-to-version (script-version script))]
    (or (nil? current-version)
        (= current-version detected-version))))


(defn to-correct-scripts [scripts detected-version detected-version-index]
  (map-indexed (fn [idx script]
                 {:warning (cond
                             (not (correct-script? script detected-version)) consts/script-style-warning
                             (and (script-version script)
                                  (< idx detected-version-index)) consts/script-order-warning)
                  :script  script})
               scripts))


(defn to-correct-styles [styles detected-version]
  (map (fn [style]
         {:warning (when-not (correct-script? style detected-version) consts/script-style-warning)
          :style   style})
       styles))


(defn replace-version [version script]
  (let [version (version-to-url version)]
    (string/replace script #"^https?://cdn.anychart.com/(releases|js|css|themes)/([^/]+)/.*$"
                    (fn [[s type old-version]]
                      (string/replace-first s (re-pattern old-version) version)))))


(defn replace-version-scripts [version scripts]
  (map #(replace-version version %) scripts))