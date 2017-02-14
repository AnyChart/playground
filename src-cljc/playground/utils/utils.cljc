(ns playground.utils.utils)

(defn released-version? [version-key]
  (re-matches #"^\d+\.\d+\.\d+$" version-key))

(defn contains-graphics-script? [scripts]
  (some (fn [script] (re-find #"graphics\.min\.js" script)) scripts))

(defn need-anychart-script? [scripts]
  (not (contains-graphics-script? scripts)))

(defn anychart-bundle-url [version-key]
  (if (released-version? version-key)
    (str "https://cdn.anychart.com/js/" version-key "/anychart-bundle.min.js")
    (str "http://static.anychart.com/js/" version-key "/anychart-bundle.min.js")))

(defn filter-csss [csss]
  (filter #(not (re-find #"anychart-ui\.min\.css" %)) csss))

(defn contains-css? [csss]
  (some (fn [item] (re-find #"anychart-ui\.min\.css" item)) csss))

(defn need-anychart-css? [scripts]
  (not (contains-css? scripts)))

(defn anychart-bundle-css-url [version-key]
  (if (released-version? version-key)
    (str "https://cdn.anychart.com/css/" version-key "/anychart-ui.min.css")
    (str "http://static.anychart.com/css/" version-key "/anychart-ui.min.css")))

(defn csss [csss version-key need-anychart-script]
  (let [filtered-css (filter-csss csss)]
    (if need-anychart-script
      (conj filtered-css (anychart-bundle-css-url version-key))
      filtered-css)))
