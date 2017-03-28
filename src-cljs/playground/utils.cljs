(ns playground.utils)

(defn log [& arr]
  (.log js/console (apply str (interpose " " arr))))