(ns playground.subs
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [playground.utils :as utils]))

(rf/reg-sub
  :editors-height
  (fn [db _] (- (:editors-height db) 102)))

;; make url like /acg/master/Column_Chart?view=iframe"
(rf/reg-sub
  :sample-iframe-url
  (fn [db _] (str "/" (-> db :sample :repo-name)
                  "/" (-> db :sample :version-name)
                  (-> db :sample :url)
                  "?view=iframe")))

(rf/reg-sub
  :code
  (fn [db _] (-> db :code)))

(rf/reg-sub
  :markup
  (fn [db _] (-> db :markup)))

(rf/reg-sub
  :style
  (fn [db _] (-> db :style)))