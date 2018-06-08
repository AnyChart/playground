(ns playground.preview-generator.download
  (:require [taoensso.timbre :as timbre]
            [clj-http.client :as http]))

;; The idea is to cache downloaded script/styles to improve preview generation speed
(def state (atom {}))


(defn check-url [url]
  (if (.startsWith url "//")
    (str "http:" url)
    url))


(defn download [state path]
  (swap! state (fn [state]
                 (if (nil? (get state path))
                   (let [{status :status data :body} (try (http/get path)
                                                          (catch Exception e
                                                            (timbre/error "failed to download for previews cache:" path)
                                                            ""))]
                     (assoc state path data))
                   state))))


(defn clear []
  (reset! state {}))


(defn get-url [url]
  (let [url (check-url url)]
    (download state url)
    (get @state url)))


(defn get-urls [urls]
  (map get-url urls))

