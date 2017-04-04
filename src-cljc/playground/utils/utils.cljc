(ns playground.utils.utils)

(defn released-version? [version-key]
  (re-matches #"^\d+\.\d+\.\d+$" version-key))

(defn sample-url [sample]
  (if (:version-id sample)
    (str "/" (:repo-name sample)
         "/" (:version-name sample)
         (:url sample))
    (str "/" (:url sample) "/" (:version sample))))