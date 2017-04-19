(ns playground.web.utils
  (:require [cognitect.transit :as transit])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)
           (org.apache.commons.lang3 RandomStringUtils)))

(defn drop-slash [s]
  (subs s 0 (dec (count s))))

(defn pack [data]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer data)
    (.toString out)))

(defn unpack [s]
  (let [in (ByteArrayInputStream. (.getBytes s))
        reader (transit/reader in :json)]
    (transit/read reader)))

(defn new-hash []
  (RandomStringUtils/randomAlphanumeric 8))