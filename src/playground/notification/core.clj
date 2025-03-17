(ns playground.notification.core
  (:require [com.stuartsierra.component :as component]
            ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
            [playground.data.config :as c]))


(defrecord Notifier [config]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))


(defn new-notifier [config]
  (map->Notifier {:config config}))


(defn application-start [notifier]
  ;; (when-not (= (c/prefix) "local")
    ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
  ;; )
)


(defn start-build [notifier project branches updated-branches removed-branches queue-index]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)


(defn complete-building [notifier project branches updated-branches removed-branches queue-index]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)


(defn complete-building-with-errors [notifier project branches updated-branches removed-branches queue-index e]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)


(defn complete-sync [notifier projects error-projects]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)


(defn start-version-building [notifier project version-name queue-index]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)


(defn complete-version-building [notifier project version queue-index]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)

(defn complete-version-building-error [notifier project version queue-index e]
  ;; ToDo: Implement Discord webhook notifications with embeds support and config specs
)
