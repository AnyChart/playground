(ns playground.tips.events
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]))

(rf/reg-event-db
  :tips.tip/close
  (fn [db [_ link]]
    (update-in db [:tips :current]
               #(remove (fn [tip] (= (:url tip) link)) %))))


(rf/reg-event-db
  :tips/never-show-again-change
  (fn [db [_ tip is-checked]]
    ;(utils/log is-checked (:url tip) (:type tip))
    (if is-checked
      ;; add to hidden lists
      (swap! (-> db :local-storage)
             (fn [ls] (let [ls (if (and
                                     (or (= (:type tip) :binary)
                                         (= (:type tip) :dataset))
                                     (every? #(not= (:url tip) %) (:hidden-tips ls))) ;if there isn't already the link
                                 (update ls :hidden-tips conj (:url tip))
                                 ls)
                            ls (if (and
                                     (and (not= (:type tip) :binary)
                                          (not= (:type tip) :dataset))
                                     (every? #(not= (:type tip) %) (:hidden-types ls))) ;if there isn't already the type
                                 (update ls :hidden-types conj (:type tip))
                                 ls)]
                        ls)))
      ;; remove from hidden lists
      (swap! (-> db :local-storage)
             (fn [ls]
               (-> ls
                   (update :hidden-types (fn [types] (remove #(= (:type tip) %) types)))
                   (update :hidden-tips (fn [tips] (remove #(= (:url tip) %) tips)))))))
    db))