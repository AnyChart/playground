(ns playground.editors.subs
  (:require [re-frame.core :as rf]
            [playground.tips.tips-data :as tips-data]
            [clojure.string :as string]))


(rf/reg-sub :editors/height
            (fn [db _] (-> db :editors :editors-height)))


(rf/reg-sub :editors/margin-top
            (fn [db _] (-> db :editors :editors-margin-top)))


(rf/reg-sub :editors/view
            (fn [db _] (-> db :editors :view)))


(rf/reg-sub :editors/splitter-percents
            (fn [db _]
              (let [markup-lines (-> db :sample :markup string/split-lines count)
                    style-lines (-> db :sample :style string/split-lines count)
                    ;code-lines (-> db :sample :code string/split-lines count)
                    height (-> db :editors :editors-height)

                    markup-height (+ 40 (* 17.0 markup-lines))
                    markup-height-min 70
                    markup-percent (.ceil js/Math (/ (* markup-height 100) height))
                    markup-percent-min (.ceil js/Math (/ (* markup-height-min 100) height))
                    markup-percent* (cond
                                      (< markup-percent markup-percent-min) markup-percent-min
                                      (> markup-percent 33) 33
                                      :else markup-percent)

                    style-height (+ 42 (* 17.0 style-lines))
                    style-height-min 70
                    style-percent (.ceil js/Math (/ (* style-height 100) (- height markup-height)))
                    style-percent-min (.ceil js/Math (/ (* style-height-min 100) (- height markup-height)))
                    style-percent* (cond
                                     (< style-percent style-percent-min) style-percent-min
                                     (> style-percent 50) 50
                                     :else style-percent)]
                ;(println "Calculate percent1: " markup-lines style-lines height markup-width style-width)
                ;(println "Calculate percent3: " markup-percent markup-percent-min markup-percent*)
                ;(println "Calculate percent3: " style-percent style-percent-min style-percent*)
                [markup-percent* style-percent*])))


(rf/reg-sub :editors/external-resources
            (fn [db _]
              (let [scripts (-> db :sample :scripts)
                    scripts-data (->> scripts
                                      (map #(tips-data/get-tip % db))
                                      (filter some?))]
                scripts-data)))


(rf/reg-sub :editors.code-settings/show
            (fn [db _] (-> db :editors :code-settings :show)))


(rf/reg-sub :editors/iframe-hider-show
            (fn [db _]
              (or (-> db :settings :show)
                  (-> db :embed :show)
                  (-> db :left-menu :show)
                  (-> db :view-menu :show)
                  (-> db :create-menu :show)
                  (-> db :download-menu :show)
                  (-> db :search :show))))

(rf/reg-sub :editors/iframe-update
            (fn [db _]
              (-> db :editors :iframe-update)))


; ======================================================================================================================
;; Hide or show editor COPY BUTTONS
;;======================================================================================================================
(rf/reg-sub :editors/show-code-copy-button
            (fn [db _]
              (-> db :editors :code :show-copy-button)))


(rf/reg-sub :editors/show-style-copy-button
            (fn [db _]
              (-> db :editors :style :show-copy-button)))


(rf/reg-sub :editors/show-markup-copy-button
            (fn [db _]
              (-> db :editors :markup :show-copy-button)))