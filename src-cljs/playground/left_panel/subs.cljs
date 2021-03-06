(ns playground.left-panel.subs
  (:require [re-frame.core :as rf]
            [clojure.string :as string]))


(rf/reg-sub :left-panel/collapsed
            (fn [db _]
              (-> db :left-panel :collapsed)))


(rf/reg-sub :left-panel/general-tab?
            (fn [db _]
              (= (-> db :left-panel :tab) :general)))


(rf/reg-sub :left-panel/docs-tab?
            (fn [db _]
              (= (-> db :left-panel :tab) :docs)))


(rf/reg-sub :left-panel/max-tab-height
            (fn [_ _]
              (rf/subscribe [:editors/height]))
            (fn [editors-height _]
              (- editors-height 145)))


(rf/reg-sub :left-panel/docs
            (fn [db _]
              (let [docs (-> db :left-panel :docs)
                    title-fn (fn [title]
                               (let [parts (string/split title #" \| ")]
                                 (str (first parts)
                                      (when (second parts)
                                        (str " (" (second parts) ")")))
                                 ;(string/join "/" (reverse parts))
                                 ))]
                {:articles-api  (map #(update % :title title-fn) (:articles-api docs))
                 :articles-docs (map #(update % :title title-fn) (:articles-docs docs))
                 :articles-pg   (map #(update % :title title-fn) (:articles-pg docs))})))


(rf/reg-sub :left-panel.docs/show-read-more-button?
            (fn [db _]
              (let [repo-sample (-> db :sample :version-id)
                    has-desc (-> db :sample :description not-empty)]
                (and repo-sample has-desc))))