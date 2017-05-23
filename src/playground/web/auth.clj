(ns playground.web.auth
  (:require [playground.db.request :as db-req]
            [playground.web.auth-base :refer :all]
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils :refer [response]]
            [taoensso.timbre :as timbre]))

(defn permissions-middleware [handler action]
  (fn [request]
    (let [user-permissions (or (some-> request :session :user :permissions)
                               anonymous-perms)]
      (timbre/info "permissions middleware: " user-permissions)
      (if (pos? (bit-and user-permissions (action permissions)))
        (handler request)
        (response {:error-code      0
                   :permission-code (action permissions)
                   :action          (name action)
                   :message         (str "Not permission for action: " (name action))})))))

(defn default-user []
  {:fullname    "anonymous"
   :username    (str "anonymous" (web-utils/new-hash 12))
   :permissions anonymous-perms
   :salt        nil
   :password    nil
   :email       nil})

(defn create-anonymous-user [db]
  (let [temp-user (default-user)
        user-id (db-req/add-user<! db temp-user)
        user (assoc temp-user :id user-id)]
    user))

(defn check-anonymous-middleware [handler]
  (fn [request]
    ;(timbre/info "check-anonymous-middleweare: " (-> request :session))
    (let [need-create-anonymous-user (-> request :session :user not)
          anonymous-user (when need-create-anonymous-user
                           (create-anonymous-user (get-db request)))
          request (if need-create-anonymous-user
                    (assoc-in request [:session :user] anonymous-user)
                    request)
          response-from-handler (let [resp (handler request)]
                                  (if (string? resp)
                                    (response resp)
                                    resp))
          response (if response-from-handler
                     (if (-> response-from-handler :session :user)
                       response-from-handler
                       (if need-create-anonymous-user
                         (assoc-in response-from-handler [:session :user] anonymous-user)
                         response-from-handler))
                     (do
                       (db-req/delete-user! (get-db request) {:id (:id anonymous-user)})
                       nil))]
      response)))