(ns playground.editors.tern
  (:require [ajax.core :refer [GET POST]]
            [re-frame.core :as rf]))

(def server (atom nil))

(def defs-url ["/codemirror/defs/browser.json"
               "/codemirror/defs/ecma5.json"
               "/codemirror/defs/anychart.json"
               ;; For TernJS testing
               ;; "/codemirror/defs/my.json"
               ])
(def defs (atom []))


(defn init-tern []
  ;(println "Init Tern defs")
  (let [loaded (atom 0)]
    (doseq [url defs-url]
      (GET url
           {:response-format :raw
            :handler         #(do
                                (swap! defs conj (.parse js/JSON %))
                                (swap! loaded inc)
                                (when (= @loaded (count defs-url))
                                  ;(.log js/console (clj->js {:defs @defs}))
                                  ;(.log js/console (js/CodeMirror.TernServer. (clj->js {:defs @defs})))
                                  (reset! server (js/CodeMirror.TernServer.
                                                   (clj->js {:defs          @defs
                                                             :plugins       {:doc_comment      true
                                                                             :complete_strings true}
                                                             :completionTip js/completionTip
                                                             })))
                                  ;(set! (.-updateArgHints @server)
                                  ;      (fn [cm] (js/updateArgHints @server cm)))
                                  ;(.log js/console "Server:")
                                  ;(.log js/console @server)
                                  (rf/dispatch [:tern/udpate-anychart-defs])))
            :error-handler   #(println %)}))))

