(ns playground.preview-generator.sample-preparation
  (:require [clojure.string :as string]))


(defn fix-code [code]
  (when code (string/replace code
                             (string/re-quote-replacement ".animation(true")
                             ".animation(false")))


(defn set-code [sample]
  (update sample :code (fn [code]
                         (str "if (typeof anychart !== 'undefined') {anychart.licenseKey('anychart-adb3c9ca-df1c254d');}"
                              (fix-code code)))))


(defn set-style [sample]
  (update sample :style #(str ".anychart-credits{display:none;}" %)))


(defn prepare-sample [sample]
  (-> sample
      set-code
      set-style))