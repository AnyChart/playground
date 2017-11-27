(ns playground.web.utils
  (:require [cognitect.transit :as transit]
            [buddy.core.codecs.base64 :as b64]
            [playground.db.request :as db-req])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)
           (org.apache.commons.lang3 RandomStringUtils)
           (java.security SecureRandom)
           (java.util Base64)))

(def empty-sample
  {:name              ""
   :tags              []
   :short-description ""
   :description       ""
   :url               ""

   :styles            []
   :scripts           []

   :markup            ""
   :markup-type       "html"

   :code              ""
   :code-type         "js"

   :style             ""
   :style-type        "css"})


(defn response [body]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    body})

(defn drop-slash [s]
  (subs s 0 (dec (count s))))

(defn pack [data]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer data)
    (.toString out "UTF-8")))

(defn unpack [s]
  (let [in (ByteArrayInputStream. (.getBytes s))
        reader (transit/reader in :json)]
    (transit/read reader)))

(defn new-hash [count]
  (RandomStringUtils/randomAlphanumeric count))

(defn sample-hash [db]
  (let [hash (new-hash 8)]
    (if (db-req/url-exist db {:url hash})
      (sample-hash db)
      hash)))

(defn to-base64 [byte-array]
  (String. (b64/encode byte-array) "UTF-8"))

(defn new-salt []
  (let [r (SecureRandom.)
        b (bytes (byte-array 32))]
    (.nextBytes r b)
    (to-base64 b)))