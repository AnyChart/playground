(ns playground.site.pages.landing-page-utils)

(defn title [page]
  (str "AnyChart Playground"
       (when (pos? page)
         (str ", page " (inc page)))))