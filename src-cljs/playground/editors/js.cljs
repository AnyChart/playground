(ns playground.editors.js
  (:require [re-frame.core :as rf]
            [re-frame.db :as rfdb]
            [playground.editors.tern :as tern]
            [clojure.string :as string]
            [ajax.core :refer [GET POST]]))


;; =====================================================================================================================
;; get AnyChart API methods info from code string
;; =====================================================================================================================
(defn re-seq-index
  ([re s i]
   (let [match-data (re-find re s)
         match-idx (.search s re)
         match-str (if (coll? match-data) (first match-data) match-data)
         post-match (subs s (+ match-idx (count match-str)))]
     (when match-data
       (lazy-seq (cons {:data  match-data
                        :index (+ i match-idx)}
                       (when (seq post-match)
                         (re-seq-index re
                                       post-match
                                       (+ i match-idx (count match-str)))))))))
  ([re s] (re-seq-index re s 0)))


(defn make-tern-request [cm pos callback]
  (.request @tern/server
            cm
            "type"
            (fn [error js-data]
              (if error
                (do
                  ; (println "get AnyChart Tern type error: ")
                  ; (.log js/console error)
                  (callback {}))
                (let [data (js->clj js-data :keywordize-keys true)]
                  ;(.log js/console "Found:")
                  ;(.log js/console js-data)
                  ;(.log js/console (:url data))
                  (callback data))))
            pos))


(defn get-docs-articles [url results sample version]
  (let [urls (->> results
                  (map :url)
                  (filter #(and (some? %)
                                (string/includes? % "anychart")))
                  (map #(last (string/split % #"/")))
                  distinct)]
    ; (.log js/console (pr-str urls))
    (POST url
          {:params          {:api-methods urls
                             ; :version     version
                             :version     "v8"
                             :project     (:repo-name sample)
                             :url         (:url sample)}
           :format          :json
           :response-format :json
           :keywords?       true
           :handler         #(rf/dispatch [:tern/on-get-anychart-defs %])
           :error-handler   #(println "Error " %)})))


(defn get-anychart-defs [cm url sample version]
  (let [data (.getValue cm)
        re #"\.[a-zA-Z_0-9]+\("
        indicies (re-seq-index re data)
        results (atom [])]
    (doseq [method-call indicies]
      ;(.log js/console (clj->js i))
      (let [index (:index method-call)]
        ;data (.getAnyChartDefs @tern/server cm (inc index))
        (make-tern-request cm (inc index)
                           (fn [data]
                             (let [full-data (merge method-call data)]
                               (swap! results conj full-data)
                               (when (= (count indicies)
                                        (count @results))
                                 ; (.log js/console (pr-str (map identity @results)))
                                 (get-docs-articles url @results sample version)))))))))


;; =====================================================================================================================
;; Editors creation
;; =====================================================================================================================
(def max-window-width 800)


(defn create-editor [type value mode]
  ;(utils/log "create-editor: " type value mode)
  (let [editor-name (str (name type) "-editor")
        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value          value
                                    :lineNumbers    true
                                    :mode           {:name mode}
                                    :scrollbarStyle "overlay"}))]
    (.on cm "change" (fn [cm change]
                       (rf/dispatch [:change-code type (.getValue cm)])))
    ;(rf/dispatch [:change-code type (.getValue cm)])
    cm))


(defn tern-enabled []
  (and @tern/server
       ;; TODO: consider to make it in re-frame events flow
       (-> @rfdb/app-db :editors :code :autocomplete)))


(defn create-js-editor [type value mode]
  ;(utils/log "create-editor: " type value mode)
  (let [editor-name (str (name type) "-editor")
        key-map {"Ctrl-Space" #(when (tern-enabled) (.complete @tern/server %))
                 "Ctrl-O"     #(when (tern-enabled) (.showDocs @tern/server %))
                 "Ctrl-I"     #(when (tern-enabled) (.showType @tern/server %))
                 "Alt-."      #(when (tern-enabled) (.jumpToDef @tern/server %))
                 "Alt-,"      #(when (tern-enabled) (.jumpBack @tern/server %))
                 "Ctrl-Q"     #(when (tern-enabled) (.rename @tern/server %))
                 ; "Ctrl-B"     #(when (tern-enabled) (.getAnyChartDefs @tern/server %))
                 ;"Ctrl-M"     #(when (tern-enabled) (time (get-anychart-defs % {})))
                 "Ctrl-M"     #(when (tern-enabled) (rf/dispatch [:tern/udpate-anychart-defs]))
                 }

        cm (js/CodeMirror (.getElementById js/document editor-name)
                          (clj->js {:value          value
                                    :lineNumbers    true
                                    :mode           {:name mode}
                                    :scrollbarStyle "overlay"
                                    ;:extraKeys      {"Ctrl-Space" "autocomplete"}
                                    :extraKeys      key-map}))]

    ;;  editor.on("cursorActivity", function(cm) { server.updateArgHints(cm); });
    ;(.on cm "cursorActivity" (fn [cm]
    ;                           (when @tern/server
    ;                             (js/updateArgHints @tern/server cm))))

    (.on cm "cursorActivity" (fn [cm]
                               (when (tern-enabled)
                                 (.updateArgHints @tern/server cm))))

    (.on cm "keyup" (fn [cm e]
                      (when (and
                              (tern-enabled)
                              ;;  "." character
                              (= (or (.-which e) (.-keyCode e)) 190))
                        (.complete @tern/server cm))))

    (.on cm "change" (fn [cm change]
                       (rf/dispatch [:change-code-code (.getValue cm)])))

    ;(rf/dispatch [:change-code-code (.getValue cm)])
    cm))


(defn window-height []
  (or (.-innerHeight js/window)
      (.-clientHeight (.-documentElement js/document))
      (.-clientHeight (.-body js/document))))


(defn window-width []
  (or (.-innerWidth js/window)
      (.-clientWidth (.-documentElement js/document))
      (.-clientWidth (.-body js/document))))


(defn small-window-width? []
  (< (window-width) max-window-width))


(defn big-window-width? []
  (>= (window-width) max-window-width))


(defn editors-margin-top []
  (if (< (window-width) 1060) 116 58))


(defn editors-height []
  (- (window-height)
     (editors-margin-top)                                   ; header height
     ;; 70                                                     ; footer height
     ))


(defn init []
  ;; hide or show editors copy buttons
  ;; TODO: remake it without re-frame pipeline, cause it's pollute re-frisk event history
  (js/setInterval (fn [_]
                    (let [code-editor (.getElementById js/document "code-editor")
                          style-editor (.getElementById js/document "style-editor")
                          markup-editor (.getElementById js/document "markup-editor")]
                      (when (and code-editor style-editor markup-editor)
                        (rf/dispatch [:editors/code-width-change (.-offsetWidth code-editor)])
                        (rf/dispatch [:editors/style-width-change (.-offsetWidth style-editor)])
                        (rf/dispatch [:editors/markup-width-change (.-offsetWidth markup-editor)]))))
                  50)

  (.addEventListener js/window "resize" (fn [_] (rf/dispatch [:resize-window])))
  ;; for closing code editor context menu
  (.addEventListener js/window "mouseup"
                     (fn [e]
                       (let [code-menu (.getElementById js/document "code-context-menu")
                             btn (.getElementById js/document "code-editor-settings-button")]
                         (when (and code-menu
                                    btn
                                    (not (.contains code-menu (.-target e)))
                                    (not (.contains btn (.-target e))))
                           (rf/dispatch [:editors.code.settings-menu/hide]))))))

(init)