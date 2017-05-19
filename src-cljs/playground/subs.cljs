(ns playground.subs
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [playground.utils :as utils]
            [playground.utils.utils :as common-utils]
            [playground.web.auth-base :as auth-base]))

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
  :editors-height
  (fn [db _] (:editors-height db)))

(rf/reg-sub
  :sample-url
  (fn [db _]
    (common-utils/sample-url (:sample db))))

;; make url like /acg/master/Column_Chart?view=iframe"
(rf/reg-sub
  :sample-iframe-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _]
    (when (seq sample-url)
      (str sample-url "?view=iframe"))))

(rf/reg-sub
  :sample-standalone-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _] (str sample-url "?view=standalone")))

(rf/reg-sub
  :sample-editor-url
  (fn [query_v _] (rf/subscribe [:sample-url]))
  (fn [sample-url _] (str sample-url "?view=editor")))

(rf/reg-sub :name (fn [db _] (-> db :sample :name)))
(rf/reg-sub :description (fn [db _] (-> db :sample :description)))
(rf/reg-sub :short-description (fn [db _] (-> db :sample :short-description)))
(rf/reg-sub :tags (fn [db _] (-> db :sample :tags)))


(rf/reg-sub :scripts (fn [db _] (-> db :sample :scripts)))
(rf/reg-sub :scripts-str (fn [db _] (-> db :settings :scripts-str)))
(rf/reg-sub :styles (fn [db _] (-> db :sample :styles)))
(rf/reg-sub :styles-str (fn [db _] (-> db :settings :styles-str)))
(rf/reg-sub :tags-str (fn [db _] (-> db :settings :tags-str)))



(rf/reg-sub :code-type (fn [db _] (-> db :sample :code-type code-type->str)))
(rf/reg-sub :code (fn [db _] (-> db :sample :code)))
(rf/reg-sub :markup-type (fn [db _] (-> db :sample :markup-type makrup-type->str)))
(rf/reg-sub :markup (fn [db _] (-> db :sample :markup)))
(rf/reg-sub :style-type (fn [db _] (-> db :sample :style-type style-type->str)))
(rf/reg-sub :style (fn [db _] (-> db :sample :style)))

(rf/reg-sub :settings-show (fn [db _] (-> db :settings :show)))
(rf/reg-sub :settings/general-tab? (fn [db _] (= :general (-> db :settings :tab))))
(rf/reg-sub :settings/external-tab? (fn [db _] (= :external (-> db :settings :tab))))
(rf/reg-sub :settings/data-sets-tab? (fn [db _] (= :data-sets (-> db :settings :tab))))

(rf/reg-sub :data-sets (fn [db _] (-> db :data-sets)))

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

(rf/reg-sub :view (fn [db _] (-> db :view)))

(rf/reg-sub :splitter-percents
            (fn [db _]
              (let [markup-lines (-> db :sample :markup clojure.string/split-lines count)
                    style-lines (-> db :sample :style clojure.string/split-lines count)
                    ;code-lines (-> db :sample :code clojure.string/split-lines count)
                    height (- (-> db :editors-height) 16)

                    markup-percent (.ceil js/Math (/ (* (+ 4 (* 17.0 markup-lines)) 100) height))
                    markup-percent-min (.ceil js/Math (/ (* (+ 4 (* 17.0 3)) 100) height))
                    markup-percent* (cond
                                      (< markup-percent markup-percent-min) markup-percent-min
                                      (> markup-percent 33) 33
                                      :else markup-percent)
                    style-percent (.ceil js/Math (/ (* (+ 8 (* 17.0 style-lines)) 100)
                                                    (- (* height (- 100 markup-percent) 0.01) 20)))
                    style-percent-min (.ceil js/Math (/ (* (+ 8 (* 17.0 3)) 100)
                                                        (- (* height (- 100 markup-percent) 0.01) 20)))
                    style-percent* (cond
                                     (< style-percent style-percent-min) style-percent-min
                                     (> style-percent 50) 50
                                     :else style-percent)]
                ;(utils/log "Calculate percent1: " height markup-lines style-lines)
                ;(utils/log "Calculate percent3: " markup-percent markup-percent-min markup-percent*)
                ;(utils/log "Calculate percent3: " style-percent style-percent-min style-percent*)
                [markup-percent* style-percent*])))