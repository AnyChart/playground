(ns playground.modal-window.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db :modal/show
                 (fn [db _] (assoc-in db [:modal :show] true)))


(rf/reg-event-db :modal/hide
                 (fn [db _] (assoc-in db [:modal :show] false)))


(rf/reg-event-db :modal/proceed
                 (fn [db _]
                   (-> db
                       (assoc-in [:settings :show] (-> db :modal :proceed :settings-show))
                       (assoc-in [:settings :tab] (-> db :modal :proceed :settings-tab))
                       (assoc-in [:modal :show] false))))


(def show-modal-warning
  (re-frame.core/->interceptor
    :id :show-modal-warning
    :after (fn [context]
             (let [styles (-> context :effects :db :settings :css-tab :styles)
                   scripts (-> context :effects :db :settings :javascript-tab :scripts)

                   incorrect-scripts (some false? (map :correct scripts))
                   incorrect-styles (some false? (map :correct styles))

                   was-on-scripts-tab (= (-> context :coeffects :db :settings :tab) :javascript)
                   was-on-styles-tab (= (-> context :coeffects :db :settings :tab) :css)
                   was-in-settings (-> context :coeffects :db :settings :show)

                   not-scripts-tab (not= (-> context :effects :db :settings :tab) :javascript)
                   not-styles-tab (not= (-> context :effects :db :settings :tab) :css)
                   close-settings (not (-> context :effects :db :settings :show))

                   scripts-tab-leave (and incorrect-scripts
                                          was-on-scripts-tab
                                          not-scripts-tab)

                   styles-tab-leave (and incorrect-styles
                                         was-on-styles-tab
                                         not-styles-tab)

                   settings-leave (or (and incorrect-scripts
                                           was-on-scripts-tab
                                           was-in-settings
                                           close-settings)
                                      (and incorrect-styles
                                           was-on-styles-tab
                                           was-in-settings
                                           close-settings))

                   show-modal-warning (or scripts-tab-leave styles-tab-leave settings-leave)

                   context (cond-> context
                                   scripts-tab-leave
                                   (-> (assoc-in [:effects :db :modal :show] show-modal-warning)
                                       (assoc-in [:effects :db :settings :tab] :javascript)
                                       (assoc-in [:effects :db :modal :proceed :settings-tab] (-> context :effects :db :settings :tab))
                                       (assoc-in [:effects :db :modal :proceed :settings-show] (-> context :effects :db :settings :show)))

                                   styles-tab-leave
                                   (-> (assoc-in [:effects :db :modal :show] show-modal-warning)
                                       (assoc-in [:effects :db :settings :tab] :css)
                                       (assoc-in [:effects :db :modal :proceed :settings-tab] (-> context :effects :db :settings :tab))
                                       (assoc-in [:effects :db :modal :proceed :settings-show] (-> context :effects :db :settings :show)))

                                   settings-leave
                                   (-> (assoc-in [:effects :db :modal :show] show-modal-warning)
                                       (assoc-in [:effects :db :settings :show] true)
                                       (assoc-in [:effects :db :modal :proceed :settings-tab] (-> context :effects :db :settings :tab))
                                       (assoc-in [:effects :db :modal :proceed :settings-show] (-> context :effects :db :settings :show))))]
               context))))
