(ns playground.web.handlers.session-handlers
  (:require [ring.util.response :refer [redirect file-response content-type]]
    ;; comp
            [playground.db.request :as db-req]
    ;; web
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils]
            [playground.web.auth :as auth]
            [playground.web.auth-base :as auth-base]
            [playground.web.utils :as web-utils :refer [response]]
    ;; views
            [playground.views.user.register-page :as register-view]
            [playground.views.user.auth-page :as auth-view]
            [playground.views.user.profile-page :as profile-view]
    ;; misc
            [crypto.password.bcrypt :as bcrypt]
            [taoensso.timbre :as timbre]
    ;; json parser
            [cheshire.core :refer :all]
    ;; request
            [clj-http.client :as client]
            [playground.elastic.core :as elastic]))

;; =====================================================================================================================
;; Pages
;; =====================================================================================================================
(defn signup-page [request]
  (register-view/page (get-app-data request)))

(defn signin-page [request]
  (auth-view/page (get-app-data request)))

(defn profile-page [request]
  (let [samples-page (get-pagination request)
        
        data (get-app-data request)
        user (:user data)
        offset (* 12 samples-page)
        userCollection (vec (db-req/samples-by-user (get-db request) (merge user {:offset offset, :perpage 12 })))
        
        total (count userCollection)
        
        end (<= (- total offset) 12)
        subColl (cond end
          (subvec userCollection offset)
          :else (subvec userCollection offset (+ offset 12))
        )
        max-page (elastic/get-max-page total 12)
        
        result (array-map
                :samples subColl
                :total total
                :max-page max-page
                :end end
                )]
        ; result (elastic/user-samples (get-elastic request)
        ;                             (:id user)    
        ;                             (* 12 samples-page)
        ;                             12)]
        (profile-view/page (merge (get-app-data request)
                                  {:result result
                                  :page samples-page}))))


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

(defn signGoogle [request]
  (def google
    (parse-string
          (get (client/post "https://accounts.google.com/o/oauth2/token" {
            :content-type "application/x-www-form-urlencoded"
            :form-params {
              :code (:code (:params request))
              :client_id "713517328581-rc2fikgk9kdsn07vfoohc6qffhauh849.apps.googleusercontent.com"
              :redirect_uri "http://localhost:8081/sign_google"
              :client_secret "amNUrdzllZ8mK_eJonHk_bkb"
              :grant_type "authorization_code"
            }
          }) :body)
    )
  )
  (def access_token
    (get-in
      google
    ["access_token"])
  )
  (def refresh_token
    (get-in
      google
    ["refresh_token"])
  )
  (def user_info
    (parse-string
      (get (client/get "https://www.googleapis.com/oauth2/v1/userinfo" {:query-params {:access_token access_token}}) :body)
    )
  )
  (println user_info)
  (def email
    (get-in
      user_info
    ["email"])
  )
  (def google_id
    (get-in
      user_info
    ["id"])
  )
  (def picture
    (get-in
      user_info
    ["picture"])
  )
  (def name
    (get-in
      user_info
    ["name"])
  )  
  (cond (empty? (db-req/get-user-by-uid (get-db request) {:uid (str "g_" google_id)}))
    (let  [db-user {
                  :uid (str "g_" google_id)
                  :img picture
                  :deleted false
                  :fullname  name
                  :username    email
                  :email       email
                  :password    refresh_token
                  :salt        nil
                  :permissions auth-base/base-perms}
          id (db-req/add-user<! (get-db request) db-user)
          user (assoc db-user :id id)]
          (def session (assoc (:session request) :user user))
          (-> (redirect "/")
            (assoc :session session ))
    )
    :else (let  [user (db-req/get-user-by-uid (get-db request) {:uid (str "g_" google_id)})]
                (def session (assoc (:session request) :user user))
                (-> (redirect "/")
                  (assoc :session session ))
    )
  ) 
)

(defn signGithub [request]
  (def github
    (parse-string
      (get (client/post "https://github.com/login/oauth/access_token" {
        :content-type "application/x-www-form-urlencoded"
        :accept "application/json"
        :form-params {
          :code (:code (:params request))
          :client_id "df3026bdfcaeeb79edf1"
          :redirect_uri "http://localhost:8081/sign_github"
          :client_secret "ed91f641777fac7596ed44132eee24ce8f97ffbe"
          :grant_type "authorization_code"
        }
      }) :body)
    )
  )
  (def access_token
    (get-in
      github
    ["access_token"])
  )

  (def user_info
    (parse-string
      (get (client/get "https://api.github.com/user" {:query-params {:access_token access_token}}) :body)
    )
  )
  (println user_info)
  (def login
    (get-in
      user_info
    ["login"])
  )
  (def github_id
    (get-in
      user_info
    ["id"])
  )
  (def picture
    (get-in
      user_info
    ["avatar_url"])
  )
  (def emails
    (parse-string
      (get (client/get "https://api.github.com//user/emails" {:query-params {:access_token access_token}}) :body)
    )
  )

  (def email
    (get-in
      (first (filter #(get-in % ["primary"]) (apply list emails)))
    ["email"])
  )
  (cond (empty? (db-req/get-user-by-uid (get-db request) {:uid (str "gh_" github_id)}))
    (let  [db-user {
                  :uid (str "gh_" github_id)
                  :img picture
                  :deleted false
                  :fullname   login
                  :username    login
                  :email       email
                  :password    access_token
                  :salt        nil
                  :permissions auth-base/base-perms}
          id (db-req/add-user<! (get-db request) db-user)
          user (assoc db-user :id id)]
          (def session (assoc (:session request) :user user))
          (-> (redirect "/")
            (assoc :session session ))
    )
    :else (let  [user (db-req/get-user-by-uid (get-db request) {:uid (str "gh_" github_id)})]
                (def session (assoc (:session request) :user user))
                (-> (redirect "/")
                  (assoc :session session ))
    )
  )
)