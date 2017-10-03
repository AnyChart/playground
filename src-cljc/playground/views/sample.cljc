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
       [cljs-time.format :as f])))

(defn date [date]
  #?(:clj  (f/unparse (f/formatter "MMM d") (c/from-sql-date date))
     :cljs (f/unparse (f/formatter "MMM d") (c/to-date-time date))))

(defn full-date [date]
  #?(:clj  (f/unparse (f/formatter "MMM d, yyyy") (c/from-sql-date date))
     :cljs (f/unparse (f/formatter "MMM d, yyyy") (c/to-date-time date))))

(defn title [sample]
  (str (if (s/blank? (:name sample)) "Sample" (:name sample))
       " created by " (:fullname sample)))

(defn image-alt [sample]
  (str (title sample)
       (when (seq (:short-description sample))
         (str ", " (:short-description sample)))))

(defn sample-landing [sample]
  [:div.col-lg-4.col-md-4.col-sm-6.col-xs-12
   [:div.sample-box
    [:div.iframe-height-scaling
     ;{:style (when (s/blank? (:short-description sample)) "padding-bottom: 46px; margin-bottom: 88px;")}
     {:style "margin-bottom: 88px;"}
     (if (:preview sample)
       [:a {:target "_blank" :href (utils/url sample)}
        [:img.image-preview {:src   (str (:full-url sample) "?view=preview")
                             :alt   (image-alt sample)
                             :title (image-alt sample)}]]
       [:iframe.iframe-preview {:src               (str (:full-url sample) "?view=iframe")
                                :allowfullscreen   "true"
                                :allowtransparency "true"
                                :sandbox           "allow-scripts allow-pointer-lock allow-same-origin allow-popups allow-modals allow-forms"}])]
    [:div.sample-info
     [:p.name [:a {:target "_blank"
                   :href   (utils/url sample)
                   :title  (title sample)}
               (if (s/blank? (:name sample)) "Noname sample" (:name sample))]]
     (when (seq (:short-description sample))
       [:p.description
        [:a {:target "_blank"
             :href   (utils/url sample)
             :title  (:short-description sample)}
         (:short-description sample)]])
     [:div.bottom-info
      [:p.likes-and-views
       [:a.views {:target "_blank"
                  :href   (utils/url sample)
                  :title  (str "Views: " (:views sample))}
        [:span {:class "views-count"} (:views sample)]
        [:span.glyphicon.glyphicon-eye-open.sample-icon {:aria-hidden "true"}]]
       [:span.likes {:title (str "Likes: " (:likes sample))}
        [:span {:class "views-count"} (:likes sample)]
        [:span.glyphicon.glyphicon-heart.sample-icon {:aria-hidden "true"}]]]
      [:p.author-and-date "By "
       [:span {:title (title sample)} (:fullname sample)] ", "
       [:span {:title (full-date (:create-date sample))} (date (:create-date sample))]]]]]])
