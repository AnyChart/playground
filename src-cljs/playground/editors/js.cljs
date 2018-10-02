(ns playground.editors.js
  (:require [re-frame.core :as rf]
            [re-frame.db :as rfdb]
            [playground.editors.tern :as tern]))


(def max-window-width 650)


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
    (rf/dispatch [:change-code type (.getValue cm)])
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
                 "Ctrl-Q"     #(when (tern-enabled) (.rename @tern/server %))}

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
                       (rf/dispatch [:change-code type (.getValue cm)])))

    (rf/dispatch [:change-code type (.getValue cm)])
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
     70                                                     ; footer height
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