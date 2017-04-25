(ns playground.preview-generator.phantom
  (:require [clojure.java.io :refer [file]]
            [clojure.string :refer [lower-case]]
            [clojure.java.shell :refer [sh]]
            [playground.preview-generator.download :as download]
            [selmer.parser :refer [render-file]]
            [taoensso.timbre :as timbre :refer [info]]
            [playground.utils.utils :as utils])
  (:import [org.imgscalr Scalr Scalr$Method Scalr$Mode]
           [java.awt.image BufferedImageOp BufferedImage]
           [javax.imageio ImageIO]))


(defn- fix-code [code]
  (when code (clojure.string/replace code
                                     (clojure.string/re-quote-replacement ".animation(true")
                                     ".animation(false")))

(defn image-path [images-folder sample]
  (str images-folder "/" (utils/name->url (:full-url sample)) ".png"))

(defn generate-img [phantom-engine phantom-generator images-folder sample]
  (let [code (render-file "templates/phantom.selmer"
                          {:scripts (download/get-urls (:scripts sample))
                           :styles  (download/get-urls (:styles sample))
                           :code    (fix-code (:code sample))
                           :markup  (:markup sample)
                           :style   (:style sample)})
        tmp-file (java.io.File/createTempFile "sample" ".html")
        ;image-path (str images-folder "/" (utils/name->url (:full-url sample)) ".png")
        image-path (image-path images-folder sample)]
    (info "generate-img:" phantom-engine phantom-generator images-folder (.getAbsolutePath tmp-file) image-path sample)
    (with-open [f (clojure.java.io/writer tmp-file)]
      (binding [*out* f]
        (println code)))
    (try
      (do
        (sh phantom-engine
            "--web-security=false"
            phantom-generator
            (.getAbsolutePath tmp-file)
            image-path
            "'chart draw'")
        (let [image (ImageIO/read (file image-path))
              res (Scalr/resize image
                                Scalr$Method/ULTRA_QUALITY
                                Scalr$Mode/FIT_TO_WIDTH
                                (* 2 310)
                                (* 2 150)
                                (into-array BufferedImageOp [Scalr/OP_ANTIALIAS]))]
          (ImageIO/write res "png" (file image-path)))
        (sh "pngquant" "--force" "--ext" ".png" image-path)
        (info "generated" image-path)
        ;;(.delete tmp-file)
        {:id (:id sample) :img image-path})
      (catch Exception e
        (do
          (info "generation failed for" (:url sample) "html:" (.getAbsolutePath tmp-file) "exception:" e)
          {:id (:id sample) :tmp-path (.getAbsolutePath tmp-file) :error true :e e})))))
