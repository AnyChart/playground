(ns playground.preview-generator.download)

(defn check-url [url]
  (if (.startsWith url "//")
    (str "http:" url)
    url))

(defn download [state path]
  (swap! state (fn [state]
                 (if (nil? (get state path))
                   (let [data (try
                                (slurp path)
                                (catch Exception e
                                  nil))]
                     (assoc state path data))
                   state))))

(def state (atom {}))

(defn get-url [url]
  (let [url (check-url url)]
    (download state url)
    (get @state url)))

(defn get-urls [urls]
  (map get-url urls))

