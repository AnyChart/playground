(ns playground.views.editor.editor-page
  (:require [playground.views.common :as page]
            [hiccup.page :as hiccup-page]
            [clojure.string :as string]
            [playground.utils.utils :as utils]))


(defn title [sample]
  (if (:version-id sample)
    ;; repo sample
    (let [url-parts (->> (string/split (:url sample) #"/")
                         butlast
                         reverse
                         (concat [(:name sample)])
                         (string/join " | "))
          name-title (string/replace url-parts "_" " ")]
      (str name-title
           (when (and (:repo-title sample)
                      (seq (:repo-title sample)))
             (str " | " (:repo-title sample)))
           " |  AnyChart Playground"))
    ;; user sample
    (str (:name sample)
         (when-not (:latest sample)
           (str ", v" (:version sample)))
         (when (and (:url sample) (seq (:url sample)))
           (str " | #" (:url sample)))
         " | AnyChart Playground")))


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

     [:meta {:property "og:image" :content (utils/full-sample-image-url sample)}]
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

     [:link {:type "text/css" :rel "stylesheet" :href "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic"}]
     [:link {:type "text/css" :rel "stylesheet" :href "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300"}]

     ; codemirror
     [:script {:src "/codemirror/lib/codemirror.js"}]
     [:link {:href "/codemirror/lib/codemirror.css" :rel "stylesheet"}]
     [:link {:href "/codemirror/addon/scroll/simplescrollbars.css" :rel "stylesheet"}]
     [:script {:src "/codemirror/addon/scroll/simplescrollbars.js"}]
     [:script {:src "/codemirror/mode/javascript/javascript.js"}]
     [:script {:src "/codemirror/mode/css/css.js"}]
     [:script {:src "/codemirror/mode/xml/xml.js"}]
     [:script {:src "/codemirror/mode/htmlmixed/htmlmixed.js"}]

     ; clipboard
     [:script {:src "/js/clipboard.min.js"}]

     ; splitter
     [:script {:src "/splitter/splitter.js" :type "text/javascript"}]
     [:link {:href "/splitter/splitter.css" :type "text/css" :rel "stylesheet"}]
     [:link {:href "/css/editor.css" :rel "stylesheet"}]
     [:link {:href "https://cdn.anychart.com/fonts/2.7.2/anychart.css" :rel "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&subset=greek" :type "text/css" :rel "stylesheet"}]
     [:script {:type "text/javascript"} "window.HIDE_SHARING_BUTTONS = true;"]
     page/head-tag-manager]


    [:body
     [:script {:type "text/javascript"} "window.HIDE_SHARING_BUTTONS = true;"]
     page/body-tag-manager

     [:div {:style "display: none;"}
      [:textarea#transit-data data]]

     [:div#main-container]

     (page/bottom-footer)

     [:script {:src "/js/playground.js" :type "text/javascript"}]
     [:script {:type "text/javascript"} "playground.core.run(document.getElementById('transit-data').innerText);"]

     ]))