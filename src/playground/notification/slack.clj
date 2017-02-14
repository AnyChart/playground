(ns playground.notification.slack
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :refer [generate-string]]
            [clj-http.client :as http]))

(defrecord Notifier [config]
  component/Lifecycle

  (start [this] this)
  (stop [this] this))

(defn new-notifier [config]
  (map->Notifier {:config config}))

(defn- domain [notifier] (-> notifier :config :domain))
(defn- prefix [notifier] (-> notifier :config :tag clojure.string/upper-case))

(defn- format-exception [e]
  (str e "\n\n" (apply str (interpose "\n" (.getStackTrace e)))))

(defn- notify-attach [notifier attachments & [text]]
  (http/post (str "https://anychart-team.slack.com/services/hooks/incoming-webhook?token="
                  (-> notifier :config :token))
             {:form-params
              {:payload (generate-string
                          {:text        (or text "")
                           :attachments attachments
                           :mrkdwn      true
                           :channel     (-> notifier :config :channel)
                           :username    (-> notifier :config :username)})}}))


;; generator
(defn start-build [notifier project branches removed-branches queue-index]
  (let [attachments [{:color  "#4183C4"
                      :text (str "#" queue-index " pg `" (prefix notifier) "` - start")
                      :mrkdwn_in ["text", "pretext"]
                      :fields (filter some?
                                      [{:title "Project"
                                        :value project
                                        :short true}
                                       (when (seq branches)
                                         {:title "Branches"
                                          :value (clojure.string/join ", " branches)
                                          :short true})
                                       (when (seq removed-branches)
                                         {:title "Removed branches"
                                          :value (clojure.string/join ", " removed-branches)
                                          :short true})])}]]
    (notify-attach notifier attachments)))

(defn complete-building [notifier project branches removed-branches queue-index]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - complete")
        attachments [{:color  "#36a64f"
                      :fields (filter some?
                                      [{:title "Project"
                                        :value project
                                        :short true}
                                       (when (seq branches)
                                         {:title "Branches"
                                          :value (clojure.string/join ", " branches)
                                          :short true})
                                       (when (seq removed-branches)
                                         {:title "Removed branches"
                                          :value (clojure.string/join ", " removed-branches)
                                          :short true})])}]]
    (notify-attach notifier attachments text)))

(defn complete-building-with-errors
  ([notifier project branches removed-branches queue-index]
   (complete-building-with-errors notifier project branches removed-branches queue-index nil))
  ([notifier project branches removed-branches queue-index e]
   (let [text (str "#" queue-index " pg `" (prefix notifier) "` - complete with errors"
                   (when e (str "\n```" (format-exception e) "```")))
         attachments [{:color  "danger"
                       :text text
                       :mrkdwn_in ["text", "pretext"]
                       :fields (filter some?
                                       [{:title "Project"
                                         :value project
                                         :short true}
                                        (when (seq branches)
                                          {:title "Branches"
                                           :value (clojure.string/join ", " branches)
                                           :short true})
                                        (when (seq removed-branches)
                                          {:title "Removed branches"
                                           :value (clojure.string/join ", " removed-branches)
                                           :short true})])}]]
     (notify-attach notifier attachments))))

(defn build-failed [notifier project branch queue-index & [e]]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - ~" branch "~ failed"
                  (when e (str "\n```" (format-exception e) "```")))
        attachments [{:color  "danger"
                      :text text
                      :mrkdwn_in ["text", "pretext"]
                      :fields [{:title "Project"
                                :value project
                                :short true}]}]]
    (notify-attach notifier attachments)))


(defn other-exception [notifier project error-message queue-index]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - error occurred: " error-message)
        attachments [{:color  "warning"
                      :fields [{:title "Project"
                                :value project
                                :short true}]}]]
    (notify-attach notifier attachments text)))