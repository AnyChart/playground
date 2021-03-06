(ns playground.generator.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]]
            [me.raynes.fs :as fs]
            [clojure.java.io :refer [file]]
            [toml.core :as toml]
            [cheshire.core :as json]
            [playground.generator.parser.group-parser :as group-parser]
            [playground.generator.data-sets :as data-sets]
            [playground.generator.utils :refer [copy-dir]]
            [playground.db.request :as db-req]
            [playground.db.actions :as db-actions]
            [playground.elastic.core :as elastic]
            [playground.notification.core :as notifier]
            [playground.redis.core :as redis]
            [playground.repo.git :as git]
            [playground.utils.utils :as utils]
            [crypto.password.bcrypt :as bcrypt]
            [playground.web.utils :as web-utils]
            [playground.web.auth-base :as auth-base]
            [clojure.java.shell :as shell]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]))

;; =====================================================================================================================
;; Component
;; =====================================================================================================================
(declare update-repository-by-repo-name)
(declare check-repositories)
(declare parse-templates)
(declare add-predefined-users)


(defn message-handler [generator]
  (fn [{:keys [message attemp]}]
    (timbre/info "Redis message: " message)
    (let [{repo :repo version :version :as gen-params} message]
      (update-repository-by-repo-name generator (:db generator) repo gen-params)
      (db-req/update-tags-mw! (:db generator)))
    {:status :success}))


(defrecord Generator [conf repos db redis notifier]
  component/Lifecycle

  (start [this]
    (timbre/info "Generator start" conf)
    (data-sets/parse-data-source (:db this) (:data_sources conf))
    (add-predefined-users (:db this) (:users conf))
    (check-repositories this (:db this) (:notifier this))
    (db-req/update-tags-mw! (:db this))
    (assoc this
      :redis-worker (redis/create-worker redis (-> redis :config :queue) (message-handler this))))

  (stop [this]
    (timbre/info "Generator stop")
    (redis/delete-worker (:redis-worker this))
    (dissoc this :conf)))


(defn get-repos [conf]
  (let [get-user-fn (fn [username] (first (filter #(= username (:username %)) (:users conf))))
        repos (map #(atom (-> %
                              (update :type keyword)
                              (assoc :owner (get-user-fn (:user %)))))
                   (:repositories conf))]
    repos))


(defn new-generator [conf]
  (map->Generator {:conf  {:users        (:users conf)
                           :data_sources (:data_sources conf)
                           :queue-index  (atom 0)
                           ;; images dir from previews generator
                           :images-dir   (-> conf :previews :images-dir)}
                   :repos (get-repos conf)}))


(defn get-repo-by-name [generator name]
  (let [repos (:repos generator)]
    (first (filter #(= name (-> % deref :name)) repos))))


;; =====================================================================================================================
;; Path utils
;; =====================================================================================================================
(defn repo-path [repo]
  (str (:dir repo) "/repo"))


(defn git-path [path]
  (str path "/.git"))


(defn versions-path [repo]
  (str (:dir repo) "/versions"))


(defn version-path [repo branch]
  (str (:dir repo) "/versions/" branch))

;; =====================================================================================================================
;; Init repo
;; =====================================================================================================================
(defn download-repo [repo]
  (try
    (fs/mkdirs (:dir @repo))
    (git/clone @repo (repo-path @repo))
    (catch Exception e
      (info "Download repo " (:name @repo) " error! " e)
      (fs/delete-dir (:dir @repo))
      (throw (Exception. (str "Download repo " (:name @repo) " error"))))))


(defn check-repository [generator db repo db-repo]
  (try

    (when-not (and db-repo (fs/exists? (:dir @repo)))
      (info (str "Repository \"" (:name @repo) "\" - sync...")))

    (when-not (fs/exists? (:dir @repo))
      (download-repo repo))

    (swap! repo (fn [repo] (merge db-repo repo {:git (git/get-git (git-path (repo-path repo)))})))

    (let [owner (db-req/get-user-by-username db {:username (:user @repo)})]
      (swap! repo assoc :owner owner)

      (when-not db-repo
        (let [repo-id (db-req/add-repo<! db {:name      (:name @repo)
                                             :title     (:title @repo)
                                             :templates (boolean (:templates @repo))
                                             :owner-id  (:id owner)})]
          (swap! repo assoc :id repo-id))))

    (update-repository-by-repo-name generator db (:name @repo) {})

    (info (str "Repository \"" (:name @repo) "\" - OK"))
    {:name (:name @repo)}

    (catch Exception e
      (info (str "Repository \"" (:name @repo) "\" - ERROR, check repository's settings " e))
      {:name (:name @repo) :e e})))


(defn delete-repo [db repo]
  (timbre/info "DELETE REPO: " repo)
  (jdbc/with-db-transaction [conn (:db-spec db)]
                            (db-req/delete-repo-visits! conn {:repo-id (:id repo)})
                            (db-req/delete-samples-by-repo-name! conn {:name (:name repo)})
                            (db-req/delete-versions-by-repo-name! conn {:name (:name repo)})
                            (db-req/delete-repo-by-name! conn {:name (:name repo)})))


(defn check-repositories [generator db notifier]
  (info "Synchronize repositories...")
  (let [repos (:repos generator)
        db-repos (db-req/repos db)
        get-repo-by-name-fn (fn [repo repos]
                              (first (filter #(= (:name repo) (:name %)) repos)))
        deleted-repos (remove #(get-repo-by-name-fn % (map deref repos)) db-repos)]
    (timbre/info "Delete repos: " (pr-str (map :name deleted-repos)))
    (doseq [repo deleted-repos]
      (delete-repo db repo))
    (let [result (map #(check-repository generator db % (get-repo-by-name-fn @% db-repos)) repos)]
      (notifier/complete-sync notifier
                              (remove :e result)
                              (filter :e result)))))


;; =====================================================================================================================
;; Remove branches
;; =====================================================================================================================
(defn need-remove-branch? [db-branch actual-branches]
  (every? #(not= (:name db-branch) (:name %)) actual-branches))


(defn branches-for-remove [actual-branches db-branches]
  (let [removed-branches (filter #(need-remove-branch? % actual-branches) db-branches)]
    removed-branches))


(defn remove-previews [repo-name branch-name dir]
  (let [rm (str "rm " dir "/" (utils/name->url (str repo-name "-" branch-name "-*.png")))]
    (info "Remove previews:" rm)
    (shell/sh "/bin/bash" "-c" rm)))


;; =====================================================================================================================
;; Update branches
;; =====================================================================================================================
(defn read-version-config [path]
  (when (.exists (file path))
    (some-> path slurp (toml/read :keywordize))))


(defn build-branch [db elastic redis repo branch path versions]
  (info "Build branch: " path (:name branch))
  (let [version-config (-> (read-version-config (str path "/config.toml"))
                           (assoc-in [:vars :branch-name] (:name branch)))
        samples* (group-parser/samples path version-config (:samples-filter @repo))
        ;; TODO: delete replacing urls for old sample format
        samples** (map #(update-in % [:scripts] (fn [scripts] (utils/replace-urls (:name branch) scripts))) samples*)
        samples (map #(assoc % :owner-id (-> @repo :owner :id)) samples**)
        version-id (db-req/add-version<! db {:name          (:name branch)
                                             :commit        (:commit branch)
                                             :repo-id       (:id @repo)
                                             :hidden        false
                                             :config        version-config
                                             :samples-count (count samples)})]
    (timbre/info "Insert samples: " (count samples))
    (when (seq samples)
      (let [ids (db-req/add-samples! db version-id samples)]
        ;;  set views
        (doseq [sample samples]
          (db-req/update-sample-views-from-canonical-visits! db {:url     (:url sample)
                                                                 :repo-id (:id @repo)}))
        ;(elastic/update-branch db elastic (:name @repo) (:name branch) version-id)
        (redis/generate-previews redis ids)
        ;; if repo is templates-repo, then update templates
        (when (:templates @repo)
          (db-req/delete-templates! db)
          (db-req/add-templates! db ids))))
    (timbre/info "Done samples inserting: " (count samples))
    (let [old-versions (filter #(and (= (:name %) (:name branch))
                                     (not= (:id %) version-id)) versions)]
      (info "Delete old versions for" (:name branch) ": " (pr-str (map #(select-keys % [:id :name]) old-versions)))
      (doseq [version old-versions]
        (db-actions/remove-branch db version)))
    (elastic/remove-branch elastic (:name @repo) (:name branch))
    (db-req/show-version! db {:repo-id (:id @repo) :id version-id})))


(defn update-branch [db redis repo branch versions generator queue-index]
  (try
    (info "Update branch: " branch)
    (notifier/start-version-building (:notifier generator) (:name @repo) branch queue-index)
    (let [path (version-path @repo (:name branch))
          git-path (git-path path)]
      (fs/delete-dir path)
      ;(fs/copy-dir (repo-path @repo) path)
      (copy-dir (repo-path @repo) path)
      (let [git-repo (git/get-git git-path)]
        (git/checkout git-repo (:name branch) (:commit branch))
        (git/pull git-repo @repo)
        (build-branch db (:elastic generator) redis repo branch path versions)))
    (notifier/complete-version-building (:notifier generator) (:name @repo) branch queue-index)
    nil
    (catch Exception e
      (do (error e)
          (error (.getMessage e))
          (notifier/complete-version-building-error (:notifier generator) (:name @repo) branch queue-index e)
          {:branch branch :e e}))))


(defn changed-branch? [branch db-branches]
  (let [db-branch (first (filter #(= (:name branch) (:name %)) db-branches))]
    (or (nil? db-branch)
        (not= (:commit branch) (:commit db-branch)))))


(defn changed-branches [actual-branches db-branches]
  (let [update-branches (filter #(changed-branch? % db-branches) actual-branches)]
    update-branches))


(defn need-update-branch [branch repo gen-params]
  (or (and (= (:name @repo) (:repo gen-params))
           (= (:name branch) (:version gen-params)))
      (utils/released-version? (:name branch))
      (= (:name branch) "develop")
      (= (:name branch) "master")
      (string/includes? (:message branch) "#pg")
      (string/includes? (:message branch) "#all")))


(defn names [branches]
  (doall (map :name branches)))


(defn update-repository [generator db repo gen-params]
  (let [queue-index (swap! (:queue-index (:conf generator)) inc)]
    (try
      (info "update repo: " (:git @repo))
      (git/fetch repo)
      (fs/mkdirs (versions-path @repo))
      (let [prev-latest-version (db-req/last-version db {:repo-id (:id @repo)})
            branch-list (git/branch-list (:git @repo))
            actual-branches (if (:branches @repo)
                              (filter #(re-matches (re-pattern (:branches @repo)) (:name %)) branch-list)
                              branch-list)
            db-branches (db-req/versions db {:repo-id (:id @repo)})
            changed-branches (changed-branches actual-branches db-branches)
            updated-branches (filter #(need-update-branch % repo gen-params) changed-branches)
            removed-branches (branches-for-remove actual-branches db-branches)]
        (info "Branch list: " (pr-str (names branch-list)))
        (info "Actual branches: " (pr-str (names actual-branches)))
        (info "DB branches: " (pr-str (names db-branches)))
        (info "Changed branches: " (pr-str (names changed-branches)))
        (info "Updated branches: " (pr-str (names updated-branches)))
        (info "Removed branches: " (pr-str (names removed-branches)))
        (db-req/repo-update-actual-versions! db {:id       (:id @repo)
                                                 :versions (utils/sort-versions (names actual-branches))})
        (notifier/start-build (:notifier generator)
                              (:name @repo)
                              (names changed-branches)
                              (names updated-branches)
                              (names removed-branches) queue-index)
        ;; delete old branches
        (doseq [branch removed-branches]
          (elastic/remove-branch (-> generator :elastic) (:name @repo) (:name branch))
          (db-actions/remove-branch db branch)
          (remove-previews (:name @repo) (:name branch) (-> generator :conf :images-dir)))

        ;; update branches
        (let [result (doall (map #(update-branch db (:redis generator) repo % db-branches generator queue-index) updated-branches))
              errors (filter some? result)]
          ;; update latest field
          (let [latest-version (db-req/last-version db {:repo-id (:id @repo)})]
            (timbre/info "Prev latest version:" (:id prev-latest-version) (:name prev-latest-version))
            (timbre/info "Latest version:" (:id latest-version) (:name latest-version))
            (db-req/update-version-samples-latest! db {:latest     false
                                                       :version-id (:id prev-latest-version)})
            (db-req/update-version-samples-latest! db {:latest     true
                                                       :version-id (:id latest-version)}))

          (fs/delete-dir (versions-path @repo))
          (if (not-empty errors)
            (notifier/complete-building-with-errors (:notifier generator)
                                                    (:name @repo)
                                                    (names changed-branches)
                                                    (names updated-branches)
                                                    (names removed-branches)
                                                    queue-index
                                                    (-> errors first :e))
            (notifier/complete-building (:notifier generator)
                                        (:name @repo)
                                        (names changed-branches)
                                        (names updated-branches)
                                        (names removed-branches)
                                        queue-index))))
      (catch Exception e
        (do (error e)
            (error (.getMessage e))
            (notifier/complete-building-with-errors (:notifier generator) (:name @repo) [] [] [] queue-index e))))))


(defn update-repository-by-repo-name [generator db repo-name gen-params]
  (update-repository generator db (get-repo-by-name generator repo-name) gen-params))


;(defn parse-templates [generator templates-config]
;  (info "Parse templates: " templates-config)
;  (let [config (read-version-config (str (:path templates-config) "/config.toml"))
;        samples (group-parser/samples (:path templates-config) config)
;        ids (db-req/add-samples! (:db generator) nil samples)
;        old-ids (db-req/templates-sample-ids (:db generator))]
;    (db-req/delete-templates! (:db generator))
;    (when (seq ids)
;      (db-req/add-templates! (:db generator) ids))
;    (when (seq old-ids)
;      (db-req/delete-samples-by-ids! (:db generator) {:ids old-ids}))))


;; =====================================================================================================================
;; Users
;; =====================================================================================================================
(defn add-predefined-users [db users]
  (doseq [user users]
    (when-not (or (db-req/get-user-by-username db {:username (:username user)})
                  (db-req/get-user-by-email db {:email (:email user)}))
      (info "Add new predefined user: " (:fullname user))
      (let [salt (web-utils/new-salt)
            hash (bcrypt/encrypt (str (:password user) salt))]
        (db-req/add-user<! db (assoc user :salt salt
                                          :password hash
                                          :permissions auth-base/base-perms))))))