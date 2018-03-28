(ns playground.site.pages.version-page-utils)


(defn title [version-name page repo-title]
  (str version-name
       (when (pos? page)
         (str ", page " (inc page)))
       " | " repo-title " | AnyChart Playground"))