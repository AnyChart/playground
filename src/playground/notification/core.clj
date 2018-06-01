(ns playground.notification.core
  (:require [com.stuartsierra.component :as component]
            [playground.notification.slack :as slack]
            [playground.notification.skype :as skype]))


(defrecord Notifier [config]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))


(defn new-notifier [config]
  (map->Notifier {:config config}))


(defn start-build [notifier project branches updated-branches removed-branches queue-index]
  (slack/start-build notifier project branches updated-branches removed-branches queue-index)
  ;(skype/start-build notifier project branches removed-branches queue-index)
  )


(defn complete-building [notifier project branches updated-branches removed-branches queue-index]
  (slack/complete-building notifier project branches updated-branches removed-branches queue-index)
  ;(skype/complete-building notifier project branches removed-branches queue-index)
  )


(defn complete-building-with-errors [notifier project branches updated-branches removed-branches queue-index e]
  (slack/complete-building-with-errors notifier project branches updated-branches removed-branches queue-index e)
  ;(skype/complete-building-with-errors notifier project branches removed-branches queue-index e)
  )


(defn complete-sync [notifier projects error-projects]
  (slack/complete-sync notifier projects error-projects))


(defn start-version-building [notifier project version-name queue-index]
  (skype/start-version-building notifier project version-name queue-index))


(defn complete-version-building [notifier project version queue-index]
  (skype/complete-version-building notifier project version queue-index))

(defn complete-version-building-error [notifier project version queue-index e]
  (skype/complete-version-building-error notifier project version queue-index e))