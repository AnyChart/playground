(ns playground.generator.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]]
            [me.raynes.fs :as fs]
            [clojure.java.io :refer [file]]
            [toml.core :as toml]
            [cheshire.core :as json]
            [playground-samples-parser.fs :as samples-fs]
            [playground.generator.parser.group-parser :as group-parser]
            [playground.generator.utils :refer [copy-dir]]
            [playground.db.request :as db-req]
            [playground.notification.slack :as slack]
            [playground.redis.core :as redis]
            [playground.repo.git :as git]))

;;============== component ==============
(declare update-repository-by-repo-name)
(declare check-repositories)
(declare parse-templates)

(defn message-handler [generator]
  (fn [{:keys [message attemp]}]
    (timbre/info "Redis message: " message)
    (update-repository-by-repo-name generator (:db generator) message)
    {:status :success}))

(defrecord Generator [conf repos db redis notifier]
  component/Lifecycle

  (start [this]
    (timbre/info "Generator start" conf)
    (check-repositories this (:db this) (:notifier this))
    ;(when (some-> conf :templates :path)
    ;  (parse-templates this (:templates conf)))
    (assoc this
      :redis-worker (redis/create-worker redis (-> redis :config :queue) (message-handler this))))

  (stop [this]
    (timbre/info "Generator stop")
    (redis/delete-worker (:redis-worker this))
    (dissoc this :conf)))

(defn new-generator [conf repos]
  (map->Generator {:conf  (assoc conf :queue-index (atom 0))
                   :repos repos}))

(defn get-repo-by-name [generator name]
  (let [repos (:repos generator)]
    (first (filter #(= name (-> % deref :name)) repos))))


;;============= path utils
(defn repo-path [repo]
  (str (:dir repo) "/repo"))

(defn git-path [path]
  (str path "/.git"))

(defn versions-path [repo]
  (str (:dir repo) "/versions"))

(defn version-path [repo branch]
  (str (:dir repo) "/versions/" branch))

;;============== init repo
(defn download-repo [repo]
  (fs/mkdirs (:dir @repo))
  (git/clone @repo (repo-path @repo)))

(defn check-repository [generator db repo db-repo]
  ;(info "Check repository: " @repo db-repo (merge db-repo @repo))
  (if (and db-repo (fs/exists? (:dir @repo)))
    (do
      (swap! repo (fn [repo] (merge db-repo repo {:git (git/get-git (git-path (repo-path repo)))})))
      (info (str "Repository \"" (:name @repo) "\" - OK"))
      {:name (:name @repo)})
    (do
      (info (str "Repository \"" (:name @repo) "\" - sync..."))
      (try
        (download-repo repo)
        (swap! repo (fn [repo] (merge db-repo repo {:git (git/get-git (git-path (repo-path repo)))
                                                    :id  (db-req/add-repo<! db {:name (:name repo)})})))
        (update-repository-by-repo-name generator db (:name @repo))
        (info (str "Repository \"" (:name @repo) "\" - OK"))
        {:name (:name @repo)}
        (catch Exception e
          (info (str "Repository \"" (:name @repo) "\" - ERROR, check repository's settings " e))
          {:name (:name @repo) :e e})))))

(defn check-repositories [generator db notifier]
  (info "Synchronize repositories...")
  (let [repos (:repos generator)
        db-repos (db-req/repos db)
        get-repo-by-name-fn (fn [repo db-repos]
                              (first (filter #(= (:name repo) (:name %)) db-repos)))]
    (info (count db-repos) (pr-str db-repos))
    (let [result (map #(check-repository generator db % (get-repo-by-name-fn @% db-repos)) repos)]
      (slack/complete-sync notifier
                           (remove :e result)
                           (filter :e result)))))


;;============ remove branches
(defn- remove-branch [db branch]
  (db-req/delete-samples! db {:version-id (:id branch)})
  (db-req/delete-version! db {:id (:id branch)}))

(defn need-remove-branch? [db-branch actual-branches]
  (every? #(not= (:name db-branch) (:name %)) actual-branches))

(defn branches-for-remove [actual-branches db-branches]
  (let [removed-branches (filter #(need-remove-branch? % actual-branches) db-branches)]
    removed-branches))


;;============ update branches
(defn read-version-config [path]
  (when (.exists (file path))
    (some-> path slurp (toml/read :keywordize))))

(defn build-branch [db repo branch path versions]
  (info "Build branch: " path (:name branch))
  (let [version-config (-> (read-version-config (str path "/config.toml"))
                           (assoc-in [:vars :branch-name] (:name branch)))
        samples (group-parser/samples path version-config)
        version-id (db-req/add-version<! db {:name          (:name branch)
                                             :commit        (:commit branch)
                                             :repo-id       (:id @repo)
                                             :hidden        true
                                             :config        (json/generate-string version-config)
                                             :samples-count (count samples)})]
    (timbre/info "Insert samples: " (count samples) version-config)
    (when (seq samples)
      (let [ids (db-req/add-samples! db version-id samples)]
        ;  ;; if repo is templates-repo, then update templates
        (when (:templates @repo)
          (db-req/delete-templates! db)
          (db-req/add-templates! db ids))))
    (timbre/info "Done samples inserting: " (count samples))
    (let [old-versions (filter #(and (= (:name %) (:name branch))
                                     (not= (:id %) version-id)) versions)]
      (info "Delete old versions for" (:name branch) ": " (pr-str old-versions))
      (doseq [version old-versions]
        (remove-branch db version)))
    (db-req/show-version! db {:repo-id (:id @repo) :id version-id})))

(defn update-branch [db repo branch versions generator queue-index]
  (try
    (info "Update branch: " branch)
    (let [path (version-path @repo (:name branch))
          git-path (git-path path)]
      (fs/delete-dir path)
      ;(fs/copy-dir (repo-path @repo) path)
      (copy-dir (repo-path @repo) path)
      (let [git-repo (git/get-git git-path)]
        (git/checkout git-repo (:name branch))
        (git/pull git-repo @repo)
        (build-branch db repo branch path versions)))
    nil
    (catch Exception e
      (do (error e)
          (error (.getMessage e))
          ;(slack/build-failed (:notifier generator) (:name project) (:name branch) queue-index e)
          {:branch branch :e e}))))

(defn need-update-branch? [branch db-branches]
  (let [db-branch (first (filter #(= (:name branch) (:name %)) db-branches))]
    (or (nil? db-branch) (not= (:commit branch) (:commit db-branch)))))

(defn branches-for-update [actual-branches db-branches]
  (let [update-branches (filter #(need-update-branch? % db-branches) actual-branches)]
    update-branches))

(defn update-repository [generator db repo]
  (let [queue-index (swap! (:queue-index (:conf generator)) inc)]
    (try
      (info "update repo: " (:git @repo))
      (git/fetch repo)
      (fs/mkdirs (versions-path @repo))
      (let [branch-list (git/branch-list (:git @repo))
            actual-branches (if (:branches @repo)
                              (filter #(re-matches (re-pattern (:branches @repo)) (:name %)) branch-list)
                              branch-list)
            db-branches (db-req/versions db {:repo-id (:id @repo)})
            updated-branches (branches-for-update actual-branches db-branches)
            removed-branches (branches-for-remove actual-branches db-branches)]
        (info "Branch list: " (pr-str (map :name branch-list)))
        (info "Actual branches: " (pr-str (map :name actual-branches)))
        (info "DB branches: " (pr-str (map :name db-branches)))
        (info "Updated branches: " (pr-str (map :name updated-branches)))
        (info "Removed branches: " (pr-str (map :name removed-branches)))
        (slack/start-build (:notifier generator) (:name @repo) (map :name updated-branches) (map :name removed-branches) queue-index)
        (doseq [branch removed-branches]
          (remove-branch db branch))
        ;(doseq [branch updated-branches]
        ;  (update-branch db repo branch db-branches generator @repo queue-index))
        (let [result (doall (map #(update-branch db repo % db-branches generator queue-index) updated-branches))
              errors (filter some? result)]
          (fs/delete-dir (versions-path @repo))
          (if (not-empty errors)
            (slack/complete-building-with-errors (:notifier generator) (:name @repo) (map :name updated-branches)
                                                 (map :name removed-branches) queue-index (-> errors first :e))
            (slack/complete-building (:notifier generator) (:name @repo) (map :name updated-branches) (map :name removed-branches) queue-index))))
      (catch Exception e
        (do (error e)
            (error (.getMessage e))
            (slack/complete-building-with-errors (:notifier generator) (:name @repo) [] [] queue-index e))))))

(defn update-repository-by-repo-name [generator db repo-name]
  (update-repository generator db (get-repo-by-name generator repo-name)))

(defn parse-templates [generator templates-config]
  (info "Parse templates: " templates-config)
  (let [config (read-version-config (str (:path templates-config) "/config.toml"))
        samples (group-parser/samples (:path templates-config) config)
        ids (db-req/add-samples! (:db generator) nil samples)
        old-ids (db-req/templates-sample-ids (:db generator))]
    (db-req/delete-templates! (:db generator))
    (when (seq ids)
      (db-req/add-templates! (:db generator) ids))
    (when (seq old-ids)
      (db-req/delete-samples-by-ids! (:db generator) {:ids old-ids}))))