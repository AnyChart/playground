(ns playground.utils
  (:require [clojure.string :as string]))

(defn log [& arr]
  (if (= 1 (count arr))
    (.log js/console (first arr))
    (.log js/console (string/join " " arr))))