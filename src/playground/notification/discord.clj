(ns playground.notification.discord
  (:require [taoensso.timbre :as timbre]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as string]
            [playground.data.config :as c]
            [playground.utils.utils :as utils]))

;; =====================================================================================================================
;; Settings and util functions
;; =====================================================================================================================
(defn- config [notifier]
  (-> notifier :config :discord))

(defn- send-webhook-message [webhook-url content & [embeds]]
  (try
    (http/post webhook-url
               {:body    (json/generate-string
                          (cond-> {:content content}
                            embeds (assoc :embeds embeds)))
                :headers {"Content-Type" "application/json"}})
    (catch Exception e
      (timbre/error "Discord send message error:" content e))))

(defn- create-embed [& {:keys [title description color fields]}]
  (cond-> {}
    title       (assoc :title title)
    description (assoc :description description)
    color       (assoc :color color)
    fields      (assoc :fields fields)))

;; Colors
(def colors
  {:success 3066993   ; Green
   :info    4620980   ; Blue
   :warning 16776960  ; Yellow
   :danger  15158332  ; Red
   })

;; Utility formatting functions
(defn bold [text] (str "**" text "**"))
(defn italic [text] (str "*" text "*"))
(defn underline [text] (str "__" text "__"))
(defn code-block [text] (str "```\n" text "\n```"))

;; =====================================================================================================================
;; Notifications functions
;; =====================================================================================================================
(defn application-start [notifier]
  (let [webhook-url (-> notifier config :webhook-url)
        content (str "**[PG " (c/prefix) "]** Application has been started")]
    (send-webhook-message webhook-url content)))

(defn send-release-message [conf version message]
  (when (and (:release-webhook-url conf)
             (utils/released-version? version)
             (= (c/prefix) "prod"))
    (send-webhook-message (:release-webhook-url conf) message)))

(defn start-version-building [notifier project {author :author
                                               commit-message :message
                                               version :name
                                               commit :commit} queue-index]
  (let [webhook-url (-> notifier config :webhook-url)
        description (str "#" queue-index " **" project "/" version "**\n"
                        "`" commit-message "`\n"
                        "Author: " author "\n"
                        "Commit: " (subs commit 0 7))
        embed (create-embed
                :title "Build Started"
                :description description
                :color (:info colors))]
    (send-webhook-message webhook-url nil [embed])))

(defn complete-version-building [notifier project {author :author
                                                  commit-message :message
                                                  version :name
                                                  commit :commit} queue-index]
  (let [webhook-url (-> notifier config :webhook-url)
        description (str "#" queue-index " **" project "/" version "**\n"
                        "`" commit-message "`\n"
                        "Author: " author "\n"
                        "Commit: " (subs commit 0 7))
        embed (create-embed
                :title "Build Completed"
                :description description
                :color (:success colors))]
    (send-webhook-message webhook-url nil [embed])))

(defn complete-version-building-error [notifier project {author :author
                                                        commit-message :message
                                                        version :name
                                                        commit :commit} queue-index e]
  (let [webhook-url (-> notifier config :webhook-url)
        description (str "#" queue-index " **" project "/" version "**\n"
                        "`" commit-message "`\n"
                        "Author: " author "\n"
                        "Commit: " (subs commit 0 7))
        error-details (when e (str "```\n" (utils/format-exception e) "\n```"))
        embed (create-embed
                :title "Build Failed"
                :description (str description "\n\n" error-details)
                :color (:danger colors))]
    (send-webhook-message webhook-url nil [embed])))

(defn start-build [notifier project branches updated-branches removed-branches queue-index]
  (let [webhook-url (-> notifier config :webhook-url)
        fields (filter some?
                      [{:name "Project"
                        :value project
                        :inline true}
                       (when (seq branches)
                         {:name "Changed branches"
                          :value (string/join ", " branches)
                          :inline true})
                       (when (seq updated-branches)
                         {:name "Branches to update"
                          :value (string/join ", " updated-branches)
                          :inline true})
                       (when (seq removed-branches)
                         {:name "Removed branches"
                          :value (string/join ", " removed-branches)
                          :inline true})])
        embed (create-embed
                :title (str "#" queue-index " Build Started")
                :description (str "**[PG " (c/prefix) "]**")
                :color (:info colors)
                :fields fields)]
    (send-webhook-message webhook-url nil [embed])))

(defn complete-building [notifier project branches updated-branches removed-branches queue-index]
  (let [webhook-url (-> notifier config :webhook-url)
        fields (filter some?
                      [{:name "Project"
                        :value project
                        :inline true}
                       (when (seq branches)
                         {:name "Changed branches"
                          :value (string/join ", " branches)
                          :inline true})
                       (when (seq updated-branches)
                         {:name "Updated branches"
                          :value (string/join ", " updated-branches)
                          :inline true})
                       (when (seq removed-branches)
                         {:name "Removed branches"
                          :value (string/join ", " removed-branches)
                          :inline true})])
        embed (create-embed
                :title (str "#" queue-index " Build Complete")
                :description (str "**[PG " (c/prefix) "]**")
                :color (:success colors)
                :fields fields)]
    (send-webhook-message webhook-url nil [embed])))

(defn complete-building-with-errors [notifier project branches updated-branches removed-branches queue-index e]
  (let [webhook-url (-> notifier config :webhook-url)
        fields (filter some?
                      [{:name "Project"
                        :value project
                        :inline true}
                       (when (seq branches)
                         {:name "Changed branches"
                          :value (string/join ", " branches)
                          :inline true})
                       (when (seq updated-branches)
                         {:name "Updated branches"
                          :value (string/join ", " updated-branches)
                          :inline true})
                       (when (seq removed-branches)
                         {:name "Removed branches"
                          :value (string/join ", " removed-branches)
                          :inline true})])
        error-details (when e (str "```\n" (utils/format-exception e) "\n```"))
        embed (create-embed
                :title (str "#" queue-index " Build Failed")
                :description (str "**[PG " (c/prefix) "]**\n\n" error-details)
                :color (:danger colors)
                :fields fields)]
    (send-webhook-message webhook-url nil [embed])))

(defn complete-sync [notifier projects error-projects]
  (let [webhook-url (-> notifier config :webhook-url)
        success-embeds (map (fn [project]
                             (create-embed
                               :title "Repository Sync Complete"
                               :description (str "Repository **" (:name project) "** synced successfully")
                               :color (:success colors)))
                           projects)
        error-embeds (map (fn [project]
                           (create-embed
                             :title "Repository Sync Failed"
                             :description (str "Repository **" (:name project) "** sync failed\n"
                                             "```\n" (utils/format-exception (:e project)) "\n```")
                             :color (:danger colors)))
                         error-projects)]
    (send-webhook-message webhook-url
                         (str "**[PG " (c/prefix) "]** Synchronization Status")
                         (concat success-embeds error-embeds))))

(defn build-failed [notifier project branch queue-index & [e]]
  (let [webhook-url (-> notifier config :webhook-url)
        description (str "#" queue-index " Branch: ~~" branch "~~\n"
                        (when e (str "```\n" (utils/format-exception e) "\n```")))
        embed (create-embed
                :title "Build Failed"
                :description description
                :color (:danger colors)
                :fields [{:name "Project"
                         :value project
                         :inline true}])]
    (send-webhook-message webhook-url nil [embed])))

(defn other-exception [notifier project error-message queue-index]
  (let [webhook-url (-> notifier config :webhook-url)
        embed (create-embed
                :title (str "#" queue-index " Error Occurred")
                :description error-message
                :color (:warning colors)
                :fields [{:name "Project"
                         :value project
                         :inline true}])]
    (send-webhook-message webhook-url nil [embed])))
