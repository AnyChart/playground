(ns playground.interceptors
  (:require [re-frame.core :as rf]))


;; Session storage is used to keep unsaved draft - modified sample, like in jsfiddle
;; You modify sample, press F5 and "Unsaved draft" message is shown
(def session-storage-sample-interceptor
  (rf/->interceptor
    :id :session-storage-sample
    :after (fn [context]
             (let [old-sample (-> context :coeffects :db :sample)
                   new-sample (-> context :effects :db :sample)]
               (if (not= old-sample new-sample)
                 (do
                   (swap! (-> context :effects :db :session-storage)
                          (fn [ss] (assoc-in ss [:sample] new-sample)))
                   ;; hide changes window warning on new changes (rewriting)
                   (assoc-in context [:effects :db :changes-window :show] false))
                 context)))))
