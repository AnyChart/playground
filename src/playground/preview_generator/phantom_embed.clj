(ns playground.preview-generator.phantom-embed
  (:require [clj-webdriver.driver :refer [init-driver]]
            [clj-webdriver.taxi :refer :all :as taxi]
            [taoensso.timbre :as timbre]
            [playground.utils.utils :as utils]
            [playground.preview-generator.download :as download]
            [clojure.string :as string])
  (:import (java.util ArrayList)
           (org.openqa.selenium.phantomjs PhantomJSDriverService PhantomJSDriver)
           (org.openqa.selenium.remote DesiredCapabilities)
           (org.openqa.selenium Dimension)
           (javax.imageio ImageIO)
           (org.imgscalr Scalr Scalr$Method Scalr$Mode)
           (java.io ByteArrayInputStream File)
           (java.awt.image BufferedImageOp)))


(def phantom-viewport-width 868)
(def phantom-viewport-height 420)

(def image-width 620)
(def image-height 300)


(defn- create-driver []
  (let [caps (DesiredCapabilities.)
        cliArgsCap (ArrayList.)]
    (.add cliArgsCap "--webdriver-loglevel=NONE")
    (.add cliArgsCap "--web-security=false")
    (.add cliArgsCap "--ssl-protocol=any")
    (.add cliArgsCap "--ignore-ssl-errors=true")
    (.setCapability caps PhantomJSDriverService/PHANTOMJS_CLI_ARGS cliArgsCap)
    (.setCapability caps PhantomJSDriverService/PHANTOMJS_GHOSTDRIVER_CLI_ARGS "--logLevel=NONE")
    (init-driver {:webdriver (PhantomJSDriver. caps)})))

(defn create-drivers []
  [(create-driver) (create-driver) (create-driver) (create-driver)])

(defn setup-queue [drivers]
  (let [queue (java.util.concurrent.ConcurrentLinkedQueue.)]
    (doseq [driver drivers]
      (.add queue driver))
    queue))


(defn- get-free-driver [drivers-queue]
  (.poll drivers-queue))

(defn- return-driver [driver drivers-queue]
  (.add drivers-queue driver))


(defn- exec-script-to-png [d sample image-path]
  ;(timbre/info "Generate image -" (:url sample) (:image-url sample) (keys sample))
  (let [prev-handles (.getWindowHandles (:webdriver d))]
    (execute-script d "window.open(\"\")")
    (let [new-handles (.getWindowHandles (:webdriver d))
          new-handle (first (clojure.set/difference (set new-handles) (set prev-handles)))
          prev-handle (first prev-handles)]
      (.window (.switchTo (:webdriver d)) new-handle)
      (.setSize (.window (.manage (:webdriver d))) (Dimension. phantom-viewport-width
                                                               phantom-viewport-height))
      ;(prn "Window handles on start: " prev-handles)
      ;(prn "Current: " (.getWindowHandle (:webdriver d)))
      (let [startup
            (try
              (execute-script d "document.body.style.margin = 0;
                                 document.body.innerHTML = arguments[0]"
                              [(:markup sample)])
              (catch Exception e (str "Failed to execute startup script\n" (.getMessage e))))

            styles
            (try
              (doseq [style (:styles-data sample)]
                (execute-script d "var css = document.createElement('style');
                                   css.type = 'text/css';
                                   css.innerHTML = arguments[0];
                                   document.body.appendChild(css);"
                                [style]))
              (catch Exception e (str "Failed to set css\n" (.getMessage e))))

            styles-urls
            (try
              (doseq [style-url (download/get-urls (:styles sample))]
                (execute-script d "var link = document.createElement('link');
                                   link.type = 'text/css';
                                   link.rel = 'stylesheet';
                                   link.href = arguments[0];
                                   document.head.appendChild(link);"
                                [style-url]))
              (catch Exception e (str "Failed to set css\n" (.getMessage e))))

            css
            (try
              (execute-script d "var css = document.createElement('style');
                                 css.type = 'text/css';
                                 css.innerHTML = arguments[0];
                                 document.body.appendChild(css);"
                              [(:style sample)])
              (catch Exception e (str "Failed to set css\n" (.getMessage e))))

            scripts
            (try
              (doseq [script (download/get-urls (:scripts sample))]
                (execute-script d "var s=window.document.createElement('script')
                                   s.innerHTML = arguments[0];
                                   window.document.head.appendChild(s);" [script]))
              (catch Exception e (str "Failed to execute external sample script\n" (.getMessage e))))

            script
            (try
              (execute-script d "var s=window.document.createElement('script')
                                 s.innerHTML = arguments[0];
                                 window.document.head.appendChild(s);" [(:code sample)])
              (catch Exception e (str "Failed to execute Script\n" (.getMessage e))))

            triggerload
            (execute-script d "var evt = window.document.createEvent('Event');
                               evt.initEvent('load', false, false);
                               window.dispatchEvent(evt);")

            waiting
            (try
              (let [now (System/currentTimeMillis)]
                (loop []
                  (if (not-empty (elements d "svg"))
                    (do (Thread/sleep 200) nil)
                    (if (> (System/currentTimeMillis) (+ now 10000))
                      (do (Thread/sleep 200) nil)
                      (do (Thread/sleep 20) (recur))))))
              (catch Exception e (str "Failed to wait for SVG\n" (.getMessage e))))

            screenshot (take-screenshot d :bytes nil)

            shutdown
            (try
              (execute-script d "while (document.body.hasChildNodes()){document.body.removeChild(document.body.lastChild);}", [])
              (catch Exception e (str "Failed to execute Shoutdown Script\n" (.getMessage e))))

            results [startup
                     styles
                     styles-urls
                     css
                     scripts
                     script
                     waiting
                     triggerload
                     shutdown]

            error (some identity results)

            scaled-image (Scalr/resize (ImageIO/read (ByteArrayInputStream. screenshot))
                                       Scalr$Method/ULTRA_QUALITY
                                       Scalr$Mode/FIT_TO_WIDTH
                                       image-width
                                       image-height
                                       (into-array BufferedImageOp [Scalr/OP_ANTIALIAS]))]

        (execute-script d "window.close(\"\")")
        (.window (.switchTo (:webdriver d)) prev-handle)
        ;(prn "Window handles on end: " (.getWindowHandles (:webdriver d)))

        ;(with-open [out (output-stream (clojure.java.io/file image-path))]
        ;  (.write out (screenshot)))

        (ImageIO/write scaled-image "png" (clojure.java.io/file image-path))

        (when error (timbre/info "ERROR:" (:image-url sample) (pr-str (filter some? results))))

        (prn "Generate preview: " (:id sample) " " (:url sample) " " error)
        (if error
          {:error error :id (:id sample) :url (:url sample)}
          {:id (:id sample)})))))


(defn image-path [images-folder sample]
  (str images-folder "/" (utils/name->url (:full-url sample)) ".png"))


(defn generate-image [sample generator]
  (try
    (if-let [driver (get-free-driver (-> generator :drivers-queue))]
      (let [images-dir (-> generator :conf :images-dir)
            image-path (image-path images-dir sample)
            res (exec-script-to-png driver sample image-path)]
        (return-driver driver (-> generator :drivers-queue))
        res)
      {:error "Driver isn't available\n" :id (:id sample) :url (:url sample)})
    (catch Exception e
      (do
        (timbre/info "generation failed for" (:url sample) " exception:" e)
        {:error e :id (:id sample) :url (:url sample)}))))
