(ns playground.notification.slack
  (:require [cheshire.core :refer [generate-string]]
            [clj-http.client :as http]
            [taoensso.timbre :as timbre]
            [clojure.string :as string]
            [playground.data.config :as c]))


(defn prefix [notifier] (-> (c/prefix) string/upper-case))
(defn token [notifier] (-> notifier :config :slack :token))
(defn channel [notifier] (-> notifier :config :slack :channel))
(defn username [notifier] (-> notifier :config :slack :username))


(defn- format-exception [e]
  (str e "\n\n" (string/join "\n" (.getStackTrace e))))


(defn console-message [attachments text]
  (let [atts (map (fn [att]
                    (str (:text att)
                         (when (and (:text att) (seq (:fields att))) " | ")
                         (string/join ", "
                                      (map
                                        (fn [field] (str (:title field) ": " (:value field)))
                                        (:fields att)))))
                  attachments)]
    (str "\n" (when text (str text "\n")) (string/join "" atts))))


(defn- notify-attach [notifier attachments & [text]]
  (try
    (http/post (str "https://anychart-team.slack.com/services/hooks/incoming-webhook?token=" (token notifier))
               {:form-params    {:payload (generate-string
                                            {:text        (or text "")
                                             :attachments attachments
                                             :mrkdwn      true
                                             :channel     (channel notifier)
                                             :username    (username notifier)})}
                :socket-timeout 5000
                :conn-timeout   5000})
    (catch Exception e
      (timbre/error "Slack notification error:" e)
      (timbre/info "Slack notification:" (console-message attachments text)))))


;; generator
(defn start-build [notifier project branches updated-branches removed-branches queue-index]
  (let [attachments [{:color     "#4183C4"
                      :text      (str "#" queue-index " pg `" (prefix notifier) "` - start")
                      :mrkdwn_in ["text", "pretext"]
                      :fields    (filter some?
                                         [{:title "Project"
                                           :value project
                                           :short true}
                                          (when (seq branches)
                                            {:title "Changed branches"
                                             :value (string/join ", " branches)
                                             :short true})
                                          (when (seq updated-branches)
                                            {:title "Branches to update"
                                             :value (string/join ", " updated-branches)
                                             :short true})
                                          (when (seq removed-branches)
                                            {:title "Removed branches"
                                             :value (string/join ", " removed-branches)
                                             :short true})])}]]
    (notify-attach notifier attachments)))


(defn complete-building [notifier project branches updated-branches removed-branches queue-index]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - complete")
        attachments [{:color     "#36a64f"
                      :text      text
                      :mrkdwn_in ["text", "pretext"]
                      :fields    (filter some?
                                         [{:title "Project"
                                           :value project
                                           :short true}
                                          (when (seq branches)
                                            {:title "Changed branches"
                                             :value (string/join ", " branches)
                                             :short true})
                                          (when (seq updated-branches)
                                            {:title "Updated branches"
                                             :value (string/join ", " updated-branches)
                                             :short true})
                                          (when (seq removed-branches)
                                            {:title "Removed branches"
                                             :value (string/join ", " removed-branches)
                                             :short true})])}]]
    (notify-attach notifier attachments)))


(defn complete-building-with-errors [notifier project branches updated-branches removed-branches queue-index e]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - complete with errors"
                  (when e (str "\n```" (format-exception e) "```")))
        attachments [{:color     "danger"
                      :text      text
                      :mrkdwn_in ["text", "pretext"]
                      :fields    (filter some?
                                         [{:title "Project"
                                           :value project
                                           :short true}
                                          (when (seq branches)
                                            {:title "Changed branches"
                                             :value (string/join ", " branches)
                                             :short true})
                                          (when (seq updated-branches)
                                            {:title "Updated branches"
                                             :value (string/join ", " updated-branches)
                                             :short true})
                                          (when (seq removed-branches)
                                            {:title "Removed branches"
                                             :value (string/join ", " removed-branches)
                                             :short true})])}]]
    (notify-attach notifier attachments)))


(defn complete-sync-message [project]
  {:color     "#36a64f"
   :mrkdwn_in ["text", "pretext"]
   :text      (str "Repository *" (:name project) "* synced\n")})


(defn complete-sync-error-message [project]
  {:color     "danger"
   :mrkdwn_in ["text", "pretext"]
   :text      (str "Repository *" (:name project) "* synced error\n"
                   "```" (format-exception (:e project)) "```")})


(defn complete-sync [notifier projects error-projects]
  (let [text (str "pg `" (prefix notifier) "` - synchronization complete")
        attachments (concat (map complete-sync-message projects)
                            (map complete-sync-error-message error-projects))]
    (notify-attach notifier attachments text)))


(defn build-failed [notifier project branch queue-index & [e]]
  (let [text (str "#" queue-index " pg `" (prefix notifier) "` - ~" branch "~ failed"
                  (when e (str "\n```" (format-exception e) "```")))
        attachments [{:color     "danger"
                      :text      text
                      :mrkdwn_in ["text", "pretext"]
                      :fields    [{:title "Project"
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