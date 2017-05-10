(ns playground.web.auth
  (:require [ring.util.response :refer [redirect response file-response]]
            [playground.db.request :as db-req]
            [playground.web.helpers :refer :all]
            [playground.web.utils :as web-utils]))

;; actions
;(def ^:const signin 1)
;(def ^:const signup 2)
;(def ^:const create-sample 4)
;(def ^:const save-sample 8)
;(def ^:const fork-sample 16)
;(def ^:const view-sample-editor 32)
;(def ^:const view-sample-standalone 64)
;(def ^:const view-sample-iframe 128)

(def ^:const permissions {:signin                 1
                          :signup                 2
                          :create-sample          4
                          :save-sample            8
                          :fork-sample            16
                          :view-sample-editor     32
                          :view-sample-standalone 64
                          :view-sample-iframe     128})

(def ^:const anonymous-perms (apply bit-or (vals permissions)))

(def ^:const base-perms (bit-xor anonymous-perms
                                 (:signin permissions)
                                 (:signup permissions)))

(defn can [user action]
  (pos? (bit-and (:permissions user) (action permissions))))

(defn permissions-middleware [handler action]
  (fn [request]
    (let [user-permissions (or (some-> request :session :user :permissions)
                               anonymous-perms)]
      (prn "permissions middleware: " user-permissions)
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
    (prn "check-anonymous-middleweare: " (-> request :session))
    (let [need-create-anonymous-user (-> request :session :user not)
          anonymous-user (when need-create-anonymous-user
                           (create-anonymous-user (get-db request)))
          request (if need-create-anonymous-user
                    (assoc-in request [:session :user] anonymous-user)
                    request)
          response-from-handler (handler request)
          response (if (-> response-from-handler :session :user)
                     response-from-handler
                     (if need-create-anonymous-user
                       (assoc-in response-from-handler [:session :user] anonymous-user)
                       response-from-handler))]
      response)))