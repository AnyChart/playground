(ns playground.utils.xml-pretty
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import (javax.xml.transform.stream StreamSource StreamResult)
           (java.io StringReader StringWriter ByteArrayOutputStream)
           (javax.xml.transform TransformerFactory)
           (org.w3c.tidy Tidy)
           (org.jsoup Jsoup)))

(defn prettify [xml]
  (let [in (StreamSource.
             (StringReader. xml))
        writer (StringWriter.)
        out (StreamResult. writer)
        transformer (.newTransformer
                      (TransformerFactory/newInstance))]
    (.setOutputProperty transformer
                        javax.xml.transform.OutputKeys/INDENT "yes")
    (.setOutputProperty transformer
                        "{http://xml.apache.org/xslt}indent-amount" "2")
    (.setOutputProperty transformer
                        javax.xml.transform.OutputKeys/METHOD "xml")
    (.transform transformer in out)
    (-> out .getWriter .toString)))


(defn make-tidy
  "generate an instance of a preconfigured tidy object"
  []
  (doto (Tidy.)
    (.setXHTML true)
    (.setQuiet true)
    (.setShowWarnings false)
    (.setWrapSection false)
    (.setIndentContent true)
    (.setWraplen 0)
    (.setSmartIndent false)
    (.setWrapScriptlets false)
    (.setWrapAttVals false)
    (.setWriteback false)))


(defn tidy-up
  "tidy up an xml string"
  [string-to-tidy]
  (if (string? string-to-tidy)
    (let [encoding "UTF-8"]
      (with-open [outputstream (ByteArrayOutputStream.)
                  inputstream (io/input-stream
                                (.getBytes string-to-tidy encoding))]
        (.parse (make-tidy) inputstream outputstream)
        (let [tidy-html (.toString outputstream encoding)]
          (string/replace tidy-html (re-pattern "<?xml version=\"1.0\" encoding=\"UTF-8\"?>") "")
          tidy-html)))
    string-to-tidy))


(defn pretty [html]
  (try
    (let [doc (Jsoup/parse html)]
      (.toString doc))
    (catch Exception _ html)))