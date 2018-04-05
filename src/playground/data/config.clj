(ns playground.data.config)


(defonce common nil)

(defn set-config [conf] (alter-var-root (var common) (constantly conf)))

(defn commit [] (:commit common))