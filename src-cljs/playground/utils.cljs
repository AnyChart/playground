(ns playground.utils)

(defn log [& arr]
  (if (= 1 (count arr))
    (.log js/console (first arr))
    (.log js/console (clojure.string/join " " arr))))