(ns playground.tips.events
  (:require [re-frame.core :as rf]
            [playground.utils :as utils]))

(rf/reg-event-db
  :tips.tip/close
  (fn [db [_ link]]
    (update-in db [:tips :current]
               #(remove (fn [tip] (= (:link tip) link)) %))))


(rf/reg-event-db
  :tips/never-show-again-change
  (fn [db [_ tip is-checked]]
    (utils/log is-checked (:link tip) (:type tip))
    (if is-checked
      ;; add to hidden lists
      (swap! (-> db :local-storage)
             (fn [ls] (let [ls (if (and
                                     (= :binary (:type tip))
                                     (every? #(not= (:link tip) %) (:hidden-tips ls))) ;if there isn't already the link
                                 (update ls :hidden-tips conj (:link tip))
                                 ls)
                            ls (if (not= :binary (:type tip))
                                 (update ls :hidden-types conj (:type tip))
                                 ls)]
                        ls)))
      ;; remove from hidden lists
      (swap! (-> db :local-storage)
             (fn [ls]
               (-> ls
                   (update :hidden-types (fn [types] (remove #(= (:type tip) %) types)))
                   (update :hidden-tips (fn [tips] (remove #(= (:link tip) %) tips)))))))
    db))