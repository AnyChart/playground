(ns playground.db.actions
  (:require [taoensso.timbre :as timbre]
            [playground.db.request :as db-req]
            [clojure.java.jdbc :as jdbc]))


;; TODO: test behaviour, if it works well? make func/macros for transaction retry
(defn remove-branch
  ([db branch retry]
   (timbre/info "Remove (previous) branch:" retry (:name branch))
   (if (pos? retry)
     (try
       (jdbc/with-db-transaction [conn (:db-spec db) {:isolation :serializable}]
                                 (db-req/delete-version-visits! conn {:version-id (:id branch)})
                                 (db-req/delete-samples! conn {:version-id (:id branch)})
                                 (db-req/delete-version! conn {:id (:id branch)}))
       (catch Exception e
         (timbre/error "REMOVE BRANCH ERROR:" e)
         (remove-branch db branch (dec retry))))
     (do
       (timbre/info "REMOVE BRANCH retry exceeded")
       (throw (Exception. (str "REMOVE BRANCH " (:name branch) " ERROR"))))))
  ([db branch] (remove-branch db branch 100)))