(ns playground.web.handlers.sample.zip
  (:require [playground.web.helpers :refer :all]
            [clj-http.client :as http]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [playground.utils.utils :as utils]
            [playground.views.iframe :as iframe-view]
            [playground.utils.xml-pretty :as xml-pretty]
            [hiccup.core :as hiccup])
  (:import (java.util.zip ZipOutputStream ZipEntry)))


(defn get-entry-name [folder-path file-path]
  (str (subs file-path (count folder-path))))


(defn generate-zip [folder zip-path]
  (with-open [zip (ZipOutputStream. (io/output-stream zip-path))]
    (doseq [f (file-seq (io/file folder)) :when (.isFile f)]
      (.putNextEntry zip (ZipEntry. (get-entry-name folder (.getPath f))))
      (io/copy f zip)
      (.closeEntry zip))))


(defn name-from-url [url]
  (let [file-name (last (string/split (first (string/split url #"#")) #"/"))]
    file-name))


(defn create-scripts [scripts zip-folder]
  (when (seq scripts)
    (fs/mkdirs (str zip-folder "js/"))
    (doseq [script scripts]
      (let [file-name (name-from-url script)
            full-name (str zip-folder "js/" file-name)
            body (try
                   (:body (http/get script))
                   (catch Exception e nil))]
        (spit (io/file full-name) body)))))


(defn create-styles [styles zip-folder]
  (when (seq styles)
    (fs/mkdirs (str zip-folder "css/"))
    (doseq [style styles]
      (let [file-name (name-from-url style)
            full-name (str zip-folder "css/" file-name)
            body (try
                   (:body (http/get style))
                   (catch Exception e nil))]
        (spit (io/file full-name) body)))))


(defn download [request]
  (let [main-zip-folder (get-zip-folder request)

        sample (get-sample request)

        folder-name (utils/name->url (str (:name sample) (utils/sample-url sample)))

        zip-folder (str main-zip-folder folder-name "/")
        zip-archive-name (str folder-name ".zip")
        zip-archive-path (str main-zip-folder folder-name ".zip")

        scripts (:scripts sample)
        styles (:styles sample)

        html (str "<!DOCTYPE html>\n"
                  (hiccup/html (iframe-view/iframe sample)))

        html (reduce (fn [html script]
                       (string/replace html
                                       (re-pattern script)
                                       (str "js/" (name-from-url script))))
                     html scripts)

        html (reduce (fn [html style]
                       (string/replace html
                                       (re-pattern style)
                                       (str "css/" (name-from-url style))))
                     html styles)

        html (xml-pretty/pretty html)

        html-file (str zip-folder "index.html")]

    (fs/mkdirs zip-folder)

    (spit (io/file html-file) html)

    (create-scripts scripts zip-folder)

    (create-styles styles zip-folder)

    (generate-zip zip-folder zip-archive-path)

    (fs/delete-dir zip-folder)

    [zip-archive-path zip-archive-name]))