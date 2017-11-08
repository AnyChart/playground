(ns playground.site.pages.chart-type-page-utils)

(defn title [chart-type-name page]
  (str chart-type-name
       (when (pos? page)
         (str ", page " (inc page)))
       " | Chart Types | AnyChart Playground"))