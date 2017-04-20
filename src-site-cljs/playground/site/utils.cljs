(ns playground.site.utils)

(defn log [& arr]
  (.log js/console (apply str (interpose " " arr))))