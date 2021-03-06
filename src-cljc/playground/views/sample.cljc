(ns playground.views.sample
  (:require [clojure.string :as s]
            [playground.utils.utils :as utils]
    #?(:cljs
       [cljs-time.coerce :as c]
       :clj
            [clj-time.coerce :as c])
    #?(:clj
            [clj-time.format :as f]
       :cljs
       [cljs-time.format :as f])
            [clojure.string :as string]))


(defn date [date]
  #?(:clj  (f/unparse (f/formatter "MMM d") (cond
                                              (string? date) (c/from-string date)
                                              :else (c/from-sql-date date)))
     :cljs (f/unparse (f/formatter "MMM d") (c/to-date-time date))))


(defn full-date [date]
  #?(:clj  (f/unparse (f/formatter "MMM d, yyyy") (cond
                                                    (string? date) (c/from-string date)
                                                    :else (c/from-sql-date date)))
     :cljs (f/unparse (f/formatter "MMM d, yyyy") (c/to-date-time date))))


(defn title [sample]
  (str (if (s/blank? (:name sample)) "Sample" (:name sample))
       " created by " (:fullname sample)))


(defn image-alt [sample]
  (str (title sample)
       (when (seq (:short-description sample))
         (str ", " (:short-description sample)))))


(defn repo-prefix [sample]
  (when (:version-id sample)
    (if (= "api" (:repo-name sample))
      "API"
      (string/capitalize (:repo-name sample)))
    ;(first (string/split (:repo-title sample) #" "))
    ))


(defn name-sample [sample]
  (if (s/blank? (:name sample))
    "Noname sample"
    (:name sample)))


(defn long-name [sample]
  (if (:version-id sample)
    (str (repo-prefix sample) " / " (name-sample sample))
    (name-sample sample)))


(defn fake-samples
  "Need for flexbox, so last samples on last row don't scale to all raw space,
  i.e. to prevent this:
  [_]  [_]  [_]
  [_]  [_]  [_]
  [____]  [___]"
  []
  (repeat 5 [:div.col [:div.sample-box-fake]]))


(defn sample-view [sample]
  [:div.col                                                 ;col-lg-4.col-md-6.col-sm-6.col-xs-12
   [:div.sample-box
    [:div.iframe-height-scaling
     ;{:style (when (s/blank? (:short-description sample)) "padding-bottom: 46px; margin-bottom: 88px;")}
     {:style "margin-bottom: 88px;"}
     (if (:preview sample)
       [:a {:target "_blank" :href (utils/url sample)}
        [:img.image-preview {:src   (utils/sample-image-url sample)
                             :alt   (image-alt sample)
                             :title (image-alt sample)}]]
       [:div
        [:iframe.iframe-preview {:src               (utils/sample-iframe-url sample)
                                 :allowfullscreen   "true"
                                 :allowtransparency "true"
                                 :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"}]])]
    [:div.sample-info
     [:p.name [:a {:target "_blank"
                   :href   (utils/url sample)
                   :title  (title sample)}
               (long-name sample)]]
     (when (seq (:short-description sample))
       [:p.description
        [:a {:target "_blank"
             :href   (utils/url sample)
             :title  (:short-description sample)}
         (:short-description sample)]])
     [:div.bottom-info
      [:div.author-and-date "By "
       [:span {:title (title sample)} (:fullname sample)] ", "
       [:span {:title (full-date (:create-date sample))} (date (:create-date sample))]]
      [:div.likes-and-views.ml-auto
       [:a.views {:target "_blank"
                  :href   (utils/url sample)
                  :title  (str "Views: " (:views sample))}
        [:span {:class "views-count"} (:views sample)]
        ;[:span.glyphicon.glyphicon-eye-open.sample-icon {:aria-hidden "true"}]
        [:i.far.fa-eye]]
       [:span.likes {:title (str "Likes: " (:likes sample))}
        [:span {:class "views-count"} (:likes sample)]
        [:span.glyphicon.glyphicon-heart.sample-icon {:aria-hidden "true"}]]]]]]])


(defn samples [samples]
  (concat (map sample-view samples)
          (fake-samples)))