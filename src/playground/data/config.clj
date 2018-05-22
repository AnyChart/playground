(ns playground.data.config)

;; Utils
(defonce data nil)

(defn set-config [conf] (alter-var-root (var data) (constantly conf)))


;; Getters
(defn prefix [] (-> data :common :prefix))

(defn commit [] (:commit data))

(defn repos-for-versions [] (-> data :editor :repos-for-versions))

(defn released-versions [] (-> data :editor :released-versions))