(ns playground.editors.subs
  (:require [re-frame.core :as rf]
            [playground.tips.tips-data :as tips-data]))


(rf/reg-sub :editors/height
            (fn [db _] (-> db :editors :editors-height)))


(rf/reg-sub :editors/view
            (fn [db _] (-> db :editors :view)))


(rf/reg-sub :editors/splitter-percents
            (fn [db _]
              (let [markup-lines (-> db :sample :markup clojure.string/split-lines count)
                    style-lines (-> db :sample :style clojure.string/split-lines count)
                    ;code-lines (-> db :sample :code clojure.string/split-lines count)
                    height (- (-> db :editors :editors-height) 16)

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
                ;(println "Calculate percent1: " height markup-lines style-lines)
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