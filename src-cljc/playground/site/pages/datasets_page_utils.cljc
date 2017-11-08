(ns playground.site.pages.datasets-page-utils)

(defn title [page]
  (str "Data Sets"
       (when (pos? page) (str ", page " (inc page)))
       " | AnyChart Playground"))