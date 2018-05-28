(ns playground.repo.git
  (:import (org.eclipse.jgit.api Git TransportConfigCallback)
           (org.eclipse.jgit.transport UsernamePasswordCredentialsProvider JschConfigSessionFactory SshSessionFactory TagOpt RefSpec)
           (org.eclipse.jgit.util FS)
           (java.io File)
           (org.eclipse.jgit.revwalk RevWalk)
           (org.eclipse.jgit.api.errors DetachedHeadException)
           (java.util ArrayList))
  (:require [clojure.java.shell :refer [sh with-sh-env with-sh-dir]]
            [me.raynes.fs :as fs]
    ;[gita.core :refer :all]
            [clojure.string :refer [split]]
            [clojure.string :as string]))


;(def ^:dynamic *ssh-session-config* {"StrictHostKeyChecking" "no"
;                                     "UserKnownHostsFile" "~/.ssh/known_hosts"})

;(SshSessionFactory/setInstance (proxy [JschConfigSessionFactory] []
;                                 (configure [host session]
;                                   (.setPassword session password)
;                                   (let [jsch (.getJSch this host FS/DETECTED)]
;                                     ;(doseq [[key val] *ssh-session-config*]
;                                     ;  (.setConfig session key val))
;                                     (.addIdentity jsch "jgit-identity"
;                                                   (.getBytes (slurp "/media/ssd/sibental/keys/id_rsa"))
;                                                   (.getBytes (slurp "/media/ssd/sibental/keys/id_rsa.pub"))
;                                                   (.getBytes "nbvcxz"))))))


;;======================================================================================================================
;; main
;;======================================================================================================================
(defn get-git [path]
  (Git/open (File. path)))


(defn get-trasport-config-callback [secret-key-path public-key-path passphrase]
  (let [transportConfigCallback (proxy [TransportConfigCallback] []
                                  (configure [transport]
                                    (.setSshSessionFactory transport
                                                           (proxy [JschConfigSessionFactory] []
                                                             (configure [host session]
                                                               (.setConfig session "StrictHostKeyChecking" "no")
                                                               (let [jsch (.getJSch this host FS/DETECTED)]
                                                                 (.addIdentity jsch "jgit-identity"
                                                                               (.getBytes (slurp secret-key-path))
                                                                               (.getBytes (slurp public-key-path))
                                                                               (.getBytes passphrase))))))))]
    transportConfigCallback))


;;======================================================================================================================
;; clone
;;======================================================================================================================
(defn clone-http [remote-path local-path & [user password]]
  (fs/delete-dir local-path)
  (fs/mkdirs local-path)
  (let [clone-command (Git/cloneRepository)]
    (.setURI clone-command remote-path)
    (.setDirectory clone-command (File. local-path))
    (when (and user password)
      (.setCredentialsProvider clone-command (UsernamePasswordCredentialsProvider. user password)))
    (.call clone-command)))


(defn clone-ssh [remote-path local-path & [secret-key-path public-key-path passphraze]]
  (fs/delete-dir local-path)
  (fs/mkdirs local-path)
  (let [clone-command (Git/cloneRepository)]
    (.setURI clone-command remote-path)
    (.setDirectory clone-command (File. local-path))
    (when (and secret-key-path public-key-path passphraze)
      (.setTransportConfigCallback clone-command (get-trasport-config-callback secret-key-path public-key-path passphraze)))
    (.call clone-command)))


(defn clone [repo path]
  (let [type (:type repo)
        data (get repo type)]
    (case type
      :ssh (clone-ssh (:ssh data) path (:secret-key data) (:public-key data) (:passphrase data))
      :https (clone-http (:https data) path (:login data) (:password data)))))


;;======================================================================================================================
;; fetch
;;======================================================================================================================
(defn fetch-ssh [git & [secret-key-path public-key-path passphraze]]
  (let [fetch-command (.fetch git)
        specs (ArrayList.)]
    (.add specs (RefSpec. "+refs/tags/*:refs/tags/*"))
    (.setRemoveDeletedRefs fetch-command true)
    (.setTagOpt fetch-command TagOpt/FETCH_TAGS)
    (.setRefSpecs fetch-command specs)
    (.setTransportConfigCallback fetch-command (get-trasport-config-callback secret-key-path public-key-path passphraze))
    (.call fetch-command)))


(defn fetch-http [git & [user password]]
  (let [fetch-command (.fetch git)
        specs (ArrayList.)]
    (.add specs (RefSpec. "+refs/tags/*:refs/tags/*"))
    (.setRemoveDeletedRefs fetch-command true)
    (.setTagOpt fetch-command TagOpt/FETCH_TAGS)
    (.setRefSpecs fetch-command specs)
    (when (and user password)
      (.setCredentialsProvider fetch-command (UsernamePasswordCredentialsProvider. user password)))
    (.call fetch-command)))


(defn fetch [repo]
  (case (:type @repo)
    :ssh (fetch-ssh (:git @repo) (-> @repo :ssh :secret-key) (-> @repo :ssh :public-key) (-> @repo :ssh :passphrase))
    :https (fetch-http (:git @repo) (-> @repo :https :login) (-> @repo :https :password))))


;;======================================================================================================================
;; branches
;;======================================================================================================================
(defn full-branch-name->branch-name [branch-name]
  (last (split branch-name #"/")))


(defn branch-list-local [^Git git]
  (let [branches (-> git (.branchList) (.call))]
    (map #(full-branch-name->branch-name (.getName %)) (seq branches))))


(defn drop-last-slash [s]
  (if (string/ends-with? s "\n")
    (subs s 0 (dec (count s)))
    s))


(defn branch-list [^Git git]
  (let [branches (-> git
                     (.branchList)
                     (.setListMode org.eclipse.jgit.api.ListBranchCommand$ListMode/REMOTE)
                     (.call))
        ;; for release versions (that are stored in tags)
        tags (-> git
                 (.tagList)
                 (.call))
        branches (map #(let [object-id (.getObjectId %)
                             rev-walk (RevWalk. (.getRepository git))
                             rev-commit (.parseCommit rev-walk object-id)
                             data {:name    (full-branch-name->branch-name (.getName %))
                                   :commit  (.getName object-id)
                                   :message (drop-last-slash (.getFullMessage rev-commit))
                                   :author  (.getName (.getAuthorIdent rev-commit))}]
                         (.close rev-walk)
                         data)
                      (concat branches tags))]
    (sort-by :name (distinct branches))))


(defn version-list [branches]
  (filter #(re-matches #"\d+\.\d+\.\d+" %) branches))


;;======================================================================================================================
;; checkout
;;======================================================================================================================
(defn checkout [^Git git branch-name commit]
  (-> git
      (.checkout)
      (.setName branch-name)
      (.setCreateBranch (not-any? #{branch-name} (branch-list-local git)))
      (.setForce true)
      (.setStartPoint commit)
      (.call)))


;;======================================================================================================================
;; pull
;;======================================================================================================================
(defn pull-ssh [git & [secret-key-path public-key-path passphraze]]
  (let [command (.pull git)]
    (.setTransportConfigCallback command (get-trasport-config-callback secret-key-path public-key-path passphraze))
    (.call command)))


(defn pull-http [git & [user password]]
  (let [command (.pull git)]
    (when (and user password)
      (.setCredentialsProvider command (UsernamePasswordCredentialsProvider. user password)))
    (.call command)))


(defn pull [git repo]
  (try
    (case (:type repo)
     :ssh (pull-ssh git (-> repo :ssh :secret-key) (-> repo :ssh :public-key) (-> repo :ssh :passphrase))
     :https (pull-http git (-> repo :https :login) (-> repo :https :password)))
    ;; catch pull for tag - when in released version
    (catch DetachedHeadException e nil)))


;;======================================================================================================================
;; revparse
;;======================================================================================================================
(defn current-commit [git-path]
  (let [git-repo (get-git git-path)]
    (subs (.getName (.getObjectId (.getRef (.getRepository git-repo) "HEAD"))) 0 7)))