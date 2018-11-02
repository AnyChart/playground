(ns playground.web.sessions
  (:require [ring.middleware.session.store :as session-store]
            [taoensso.timbre :as timbre]
            [playground.db.request :as db-req])
  (:import (java.util UUID)))


(defn read-db [db key]
  ;(timbre/info "read db" db key)
  (db-req/get-session db {:session key}))


(defn write-db [db key data]
  ;(timbre/info "write db" key data)
  (let [user (:user data)]
    (db-req/add-session<! db {:session key
                              :user-id (:id user)})))

(defn delete-db [db key]
  ;(timbre/info "delete db" key)
  (db-req/delete-session! db {:session key}))


;; session storage
(deftype DbStore [db]
  session-store/SessionStore
  (read-session [this key]
    {:user (read-db db key)})

  (write-session [this key data]
    (let [key (or key (str (UUID/randomUUID)))]
      (delete-db db key)
      (write-db db key data)
      key))

  (delete-session [this key]
    (delete-db db key)
    nil))


(defn create-storage [db]
  (DbStore. db))
