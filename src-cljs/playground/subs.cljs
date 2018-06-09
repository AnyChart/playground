(ns playground.subs
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [playground.utils.utils :as common-utils]
            [playground.web.auth-base :as auth-base]
            [playground.utils.utils :as utils]))


(defn- makrup-type->str [type]
  (case type
    "html" "HTML"
    "md" "Markdown"
    "haml" "Haml"
    "slim" "Slim"
    "pug" "Pug"))


(defn- style-type->str [type]
  (case type
    "css" "CSS"
    "less" "LESS"
    "sass" "Sass"))


(defn- code-type->str [type]
  (case type
    "js" "JavaScript"
    "cs" "CoffeeScript"
    "ts" "TypeScript"))


(rf/reg-sub
  :sample-url
  (fn [db _]
    (common-utils/sample-url (:sample db))))


;; make url like /acg/master/Column_Chart/iframe"
(rf/reg-sub
  :sample-iframe-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _]
    (when (seq sample-url)
      (str sample-url "/iframe"))))


(rf/reg-sub
  :sample-standalone-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _] (str sample-url "/view")))


(rf/reg-sub
  :sample-editor-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _] (str sample-url "/editor")))


(rf/reg-sub :sample (fn [db _] (-> db :sample)))
(rf/reg-sub :sample/name (fn [db _] (-> db :sample :name)))
(rf/reg-sub :sample/description (fn [db _] (-> db :sample :description)))
(rf/reg-sub :sample/short-description (fn [db _] (-> db :sample :short-description)))
(rf/reg-sub :sample/tags (fn [db _] (-> db :sample :tags)))


(rf/reg-sub :sample/stripped-description
            (fn [db _]
              (common-utils/strip-tags (-> db :sample :description))))


(rf/reg-sub :sample/stripped-short-description
            (fn [db _]
              (common-utils/strip-tags (-> db :sample :short-description))))


(rf/reg-sub :sample/scripts (fn [db _] (-> db :sample :scripts)))
(rf/reg-sub :sample/styles (fn [db _] (-> db :sample :styles)))


(rf/reg-sub :sample/code-type (fn [db _] (-> db :sample :code-type code-type->str)))
(rf/reg-sub :sample/code (fn [db _] (-> db :sample :code)))
(rf/reg-sub :sample/markup-type (fn [db _] (-> db :sample :markup-type makrup-type->str)))
(rf/reg-sub :sample/markup (fn [db _] (-> db :sample :markup)))
(rf/reg-sub :sample/style-type (fn [db _] (-> db :sample :style-type style-type->str)))
(rf/reg-sub :sample/style (fn [db _] (-> db :sample :style)))


(rf/reg-sub :sample/download-html-url
            (fn [query_v _] (rf/subscribe [:sample-url]))
            (fn [sample-url _] (str sample-url "/download")))


(rf/reg-sub :sample/download-zip-url
            (fn [query_v _] (rf/subscribe [:sample-url]))
            (fn [sample-url _] (str sample-url "/download-zip")))


(rf/reg-sub :sample/show-close-warning? (fn [db _]
                                          (not= (-> db :saved-sample)
                                                (-> db :sample))))


(rf/reg-sub :datasets (fn [db _] (-> db :datasets)))


(rf/reg-sub :user-sample? (fn [db _] (-> db :sample :version-id not)))


(rf/reg-sub :templates (fn [db _] (-> db :templates)))


(rf/reg-sub :show-save-button (fn [db _]
                                (and
                                  (= (-> db :sample :owner-id)
                                     (-> db :user :id))
                                  (-> db :sample :version-id not)
                                  (-> db :sample :new not))))


(rf/reg-sub :can-signin (fn [db _] (auth-base/can (-> db :user) :signin)))
(rf/reg-sub :can-signup (fn [db _] (auth-base/can (-> db :user) :signup)))


(rf/reg-sub :view-menu/show (fn [db _] (-> db :view-menu :show)))
(rf/reg-sub :create-menu/show (fn [db _] (-> db :create-menu :show)))
(rf/reg-sub :download-menu/show (fn [db _] (-> db :download-menu :show)))