(ns playground.web.handlers.session-handlers
  (:require [ring.util.response :refer [redirect file-response content-type]]
    ;; comp
            [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils]
            [playground.web.auth :as auth]
            [playground.web.auth-base :as auth-base]
    ;; views
            [playground.views.user.register-page :as register-view]
            [playground.views.user.auth-page :as auth-view]
            [playground.views.user.profile-page :as profile-view]
    ;; misc
            [crypto.password.bcrypt :as bcrypt]
            [taoensso.timbre :as timbre]))

;; =====================================================================================================================
;; Pages
;; =====================================================================================================================
(defn signup-page [request]
  (register-view/page (get-app-data request)))

(defn signin-page [request]
  (auth-view/page (get-app-data request)))

(defn profile-page [request]
  (profile-view/page (get-app-data request)))

;; =====================================================================================================================
;; API
;; =====================================================================================================================
(defn signup [request]
  (let [username (-> request :params :username)
        fullname (-> request :params :fullname)
        email (-> request :params :email)
        password (-> request :params :password)]
    (prn "signup" username fullname email password)
    (if (and (seq username) (seq fullname) (seq email) (seq password))
      (let [salt (web-utils/new-salt)
            hash (bcrypt/encrypt (str password salt))
            db-user {:fullname    fullname
                     :username    username
                     :email       email
                     :password    hash
                     :salt        salt
                     :permissions auth-base/base-perms}
            id (db-req/add-user<! (get-db request) db-user)
            user (assoc db-user :id id)]
        (timbre/info "signup" (str password salt) hash)
        (assoc-in (redirect "/") [:session :user] user))
      "Bad values")))

(defn signin [request]
  (let [username (-> request :params :username)
        password (-> request :params :password)]
    (prn "signin" username password)
    (if (and (seq username) (seq password))
      (if-let [user (db-req/get-user-by-username-or-email (get-db request) {:username username})]
        (if (bcrypt/check (str password (:salt user)) (:password user))
          (do
            (prn "auth: " user)
            (assoc-in (redirect "/") [:session :user] user))))
      "Bad values")))

(defn signout [request]
  (assoc-in (redirect "/") [:session :user]
            (auth/create-anonymous-user (get-db request))))