(ns playground.views.common.resources
  (:require [playground.data.config :as c]
            [clojure.java.io :as io]
            [clojure.string :as string]))


;; =====================================================================================================================
;; Embeded scripts
;; =====================================================================================================================
(def head-tag-manager "<!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-5B8NXZ');</script>
<!-- End Google Tag Manager -->")


(def body-tag-manager "<!-- Google Tag Manager (noscript) -->
<noscript>
<iframe src=\"https://www.googletagmanager.com/ns.html?id=GTM-5B8NXZ\" height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->")


;; =====================================================================================================================
;; Scripts
;; =====================================================================================================================
(def site-script [:script {:src (str "/js/site.js?v=" (c/commit))}])

(def jquery-script [:script {:src "/jquery/jquery.min.js"}])

;(defn bootstrap-script [] [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}])
(def bootstrap-script [:script {:src "/bootstrap-4.1.3-dist/js/bootstrap.bundle.min.js"}])


;; =====================================================================================================================
;; Styles
;; =====================================================================================================================
(def main-style-link
  [:link {:rel "stylesheet" :type "text/css" :href (str "/css/main.css?v=" (c/commit))}])

(def main-style (slurp (io/resource "public/css/main.css")))



(def bootstrap-style-link
  ;;[:link {:rel "stylesheet" :type "text/css" :href "/bootstrap-3.3.7-dist/css/bootstrap.min.css"}]
  [:link {:rel "stylesheet" :type "text/css" :href "/bootstrap-4.1.3-dist/css/bootstrap.min.css"}])

(def bootstrap-style
  (string/replace
    ;;(slurp (io/resource "public/bootstrap-3.3.7-dist/css/bootstrap.min.css"))
    (slurp (io/resource "public/bootstrap-4.1.3-dist/css/bootstrap.min.css"))
    #"\.\.\/fonts"
    "/bootstrap-3.3.7-dist/fonts"))
