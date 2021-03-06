(ns playground.spec.app-config
  (:require [clojure.spec.alpha :as s]))


;; =====================================================================================================================
;; App .toml config file spec
;; =====================================================================================================================
(s/def ::mode #{"full" "web" "generator" "preview-generator"})


;; =====================================================================================================================
;; Web
;; =====================================================================================================================
(s/def :web/port (s/and int? pos?))
(s/def ::web (s/keys :req-un [:web/port]))


;; =====================================================================================================================
;; Database
;; =====================================================================================================================
(s/def :db/port (s/and int? pos?))
(s/def :db/host string?)
(s/def :db/name string?)
(s/def :db/user string?)
(s/def :db/password string?)
(s/def ::db (s/keys :req-un [:db/port
                             :db/host
                             :db/name
                             :db/user
                             :db/password]))


;; =====================================================================================================================
;; Redis
;; =====================================================================================================================
(s/def :redis/port (s/and int? pos?))
(s/def :redis/host string?)
(s/def :redis/db (s/and int? #(>= % 0)))
(s/def :redis/queue string?)
(s/def :redis/preview-queue string?)
(s/def ::redis (s/keys :req-un [:redis/port
                                :redis/host
                                :redis/db
                                :redis/queue
                                :redis/preview-queue]))


;; =====================================================================================================================
;; Notifications.slack
;; =====================================================================================================================
(s/def :slack/token string?)
(s/def :slack/channel string?)
(s/def :slack/username string?)
(s/def ::slack (s/keys :req-un [:slack/token
                                :slack/channel
                                :slack/username]))

(s/def :skype/id string?)
(s/def :skype/chat-id string?)
(s/def :skype/release-chat-id string?)
(s/def :skype/key string?)
(s/def ::skype (s/keys :req-un [:skype/id
                                :skype/chat-id
                                :skype/key
                                :skype/release-chat-id]))


(s/def ::notifications (s/keys :req-un [::slack
                                        ::skype]))



;; =====================================================================================================================
;; Previews and previews.maxcdn
;; =====================================================================================================================
(s/def :previews/cdn-purge boolean?)
(s/def :previews/cdn-prefix string?)
(s/def :previews/url-prefix string?)
(s/def :previews/images-dir string?)
(s/def :previews/generator string?)

(s/def :maxcdn/alias string?)
(s/def :maxcdn/key string?)
(s/def :maxcdn/secret string?)
(s/def :maxcdn/zone-id integer?)
(s/def ::maxcdn (s/keys :req-un [:maxcdn/alias
                                 :maxcdn/key
                                 :maxcdn/secret
                                 :maxcdn/zone-id]))

(s/def ::previews (s/keys :req-un [:previews/cdn-purge
                                   :previews/cdn-prefix
                                   :previews/url-prefix
                                   :previews/images-dir
                                   :previews/phantom-engine
                                   :previews/generator
                                   ::maxcdn]))


;; =====================================================================================================================
;; Users
;; =====================================================================================================================
(s/def :user/username string?)
(s/def :user/fullname string?)
(s/def :user/email string?)
(s/def :user/password string?)
(s/def ::user (s/keys :req-un [:user/username
                               :user/fullname
                               :user/email
                               :user/password]))

(s/def ::users (s/coll-of ::user))


;; =====================================================================================================================
;; Repositories
;; =====================================================================================================================
(s/def :ssh/ssh string?)
(s/def :ssh/secret-key string?)
(s/def :ssh/public-key string?)
(s/def :ssh/passphrase string?)
(s/def :repository.type/ssh (s/keys :req-un [:ssh/ssh
                                             :ssh/secret-key
                                             :ssh/public-key
                                             :ssh/passphrase]))

(s/def :https/https string?)
(s/def :https/login string?)
(s/def :https/password string?)
(s/def :repository.type/https (s/keys :req-un [:https/https
                                               :https/login
                                               :https/password]))

(s/def :repository/name string?)
(s/def :repository/title string?)
(s/def :repository/dir string?)
(s/def :repository/user string?)
(s/def :repository/type #{"ssh" "https"})

(s/def ::repository (s/and (s/keys :req-un [:repository/name
                                            :repository/title
                                            :repository/dir
                                            :repository/user
                                            :repository/type]
                                   :opt-un [:repository.type/https
                                            :repository.type/ssh])
                           #(or (:https %) (:ssh %))))

(s/def ::repositories (s/coll-of ::repository))


;; =====================================================================================================================
;; Editor
;; =====================================================================================================================
(s/def :editor/released-versions boolean?)
(s/def :editor/repos-for-versions (s/coll-of string?))


(s/def ::editor (s/keys :req-un [:editor/released-versions
                                 :editor/repos-for-versions]))


;; =====================================================================================================================
;; Main config
;; =====================================================================================================================
(s/def ::config (s/keys :req-un [::mode
                                 ::web
                                 ::db
                                 ::editor
                                 ::redis
                                 ::notifications
                                 ::previews
                                 ::users
                                 ::repositories]))

