(ns playground.site.pages.search-page-utils)


(defn title [q page]
  (str q
       (when (pos? page)
         (str ", page " (inc page)))
       " | Search | AnyChart Playground"))