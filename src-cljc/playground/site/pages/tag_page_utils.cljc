(ns playground.site.pages.tag-page-utils)

(defn title [tag page]
  (str tag
       (when (pos? page)
         (str ", page " (inc page)))
       " | Tags | AnyChart Playground"))