(ns playground.views.editor.editor-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [clojure.string :as string]
            [playground.utils.utils :as utils]
            [playground.data.config :as c])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))


(defn title [sample]
  (if (:version-id sample)
    ;; repo sample
    (let [url-parts (->> (string/split (:url sample) #"/")
                         butlast
                         reverse
                         (concat [(:name sample)])
                         (filter #(not (string/blank? %)))
                         (string/join " | "))
          name-title (string/replace url-parts "_" " ")]
      (str name-title
           (when (seq (:repo-title sample))
             (str " | " (:repo-title sample)))
           " |  AnyChart Playground"
           (when-not (:latest sample)
             (str " | ver. " (:version-name sample)))))
    ;; user sample
    (let [res (:name sample)
          res (str res (when-not (:latest sample)
                         (str (when (seq res) ", ")
                              "v" (:version sample))))
          res (str res (when (seq (:url sample))
                         (str (when (seq res) " | ")
                              "#" (:url sample))))
          res (str res " | AnyChart Playground")]
      res)))


(defn description [sample]
  (cond
    (seq (:short-description sample)) (:short-description sample)
    (seq (:description sample)) (:description sample)
    (seq (:tags sample)) (string/join ", " (:tags sample))
    :else "AnyChart Playground is the sandbox to play with gallery, API, and documentation samples. See how JavaScript charts works, change settings, and learn useful tricks from AnyChart developers."))


(defn author? [owner-fullname]
  (and owner-fullname
       (not (.startsWith owner-fullname "anonymous"))))


(defn page [{:keys [data sample canonical-url]}]
  (hiccup-page/html5
    {:lang "en"}

    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:content "IE=edge" :http-equiv "X-UA-Compatible"}]
     [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]

     [:title (title sample)]
     [:meta {:property "og:title" :content (title sample)}]
     [:meta {:name "twitter:title" :content (title sample)}]
     [:meta {:name "description" :content (description sample)}]
     [:meta {:property "og:description" :content (description sample)}]
     [:link {:href canonical-url :rel "canonical"}]
     (when (seq (:tags sample))
       [:meta {:name "keywords" :content (string/join ", " (:tags sample))}])
     (when (author? (:owner-fullname sample))
       [:meta {:name "author" :content (:owner-fullname sample)}])

     (when-not (:new sample)
       [:meta {:property "og:image" :content (utils/full-sample-image-url sample)}])
     [:meta {:property "og:url" :content (utils/canonical-url sample)}]
     [:meta {:property "og:site_name" :content "AnyChart Playground"}]
     [:meta {:property "og:type" :content "website"}]
     [:meta {:property "article:publisher" :content "https://www.facebook.com/AnyCharts"}]
     [:meta {:property "fb:admins" :content "704106090"}]
     [:meta {:name "twitter:card" :content "summary"}]
     [:meta {:name "twitter:site" :content "@AnyChart"}]
     [:meta {:name "twitter:url" :content (utils/canonical-url sample)}]

     "<!--[if lt IE 9]><script src=\"https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js\"></script><script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script><![endif]-->"

     ; bootstrap
     [:link {:href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" :rel "stylesheet"}]
     [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"}]

     ; Latest compiled and minified JavaScript
     [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"}]

     ; codemirror
     [:link {:href "/codemirror/lib/codemirror.min.css" :rel "stylesheet"}]
     [:link {:href "/codemirror/addon/scroll/simplescrollbars.css" :rel "stylesheet"}]
     [:script {:src "/js/codemirror.min.js"}]

     ; clipboard - to copy text from editors to clipboard [:script {:src "/js/clipboard.min.js"}]
     ; splitter - split/resize editors                    [:script {:src "/splitter/splitter.js" :type "text/javascript"}]
     ; Sortable -to move scripts/styles in settings       [:script {:src "/js/Sortable.min.js"}]
     [:link {:href "/splitter/splitter.css" :type "text/css" :rel "stylesheet"}]
     [:script {:src "/js/clipboard-splitter-sortable.min.js"}]

     "<!--[if IE]>"
     [:link {:href (str "/css/editor.css?v=" (c/commit)) :rel "stylesheet"}]
     [:link {:href "https://cdn.anychart.com/fonts/2.7.2/anychart.css" :rel "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&subset=greek" :type "text/css" :rel "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic" :rel "stylesheet" :type "text/css"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300" :rel "stylesheet" :type "text/css"}]
     "<!--[if IE]>"

     [:script {:type "text/javascript"} "window.HIDE_SHARING_BUTTONS = true;"]
     page/head-tag-manager]


    [:body
     ;; styles
     [:link {:href (str "/css/editor.css?v=" (c/commit)) :rel "stylesheet"}]
     [:link {:href "https://cdn.anychart.com/fonts/2.7.2/anychart.css" :rel "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&subset=greek" :type "text/css" :rel "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic" :rel "stylesheet" :type "text/css"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300" :rel "stylesheet" :type "text/css"}]

     [:script {:type "text/javascript"} "window.HIDE_SHARING_BUTTONS = true;"]
     page/body-tag-manager

     [:div#main-container]

     (page/bottom-footer)

     [:script {:src (str "/js/playground.js?v=" (c/commit)) :type "text/javascript"}]
     [:script {:type "text/javascript"}
      (page/run-js-fn "playground.core.run" data)]]))