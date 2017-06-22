(ns playground.editors.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :editors/height
            (fn [db _] (:editors-height db)))

(rf/reg-sub :editors/view
            (fn [db _] (-> db :view)))

(rf/reg-sub :editors/splitter-percents
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