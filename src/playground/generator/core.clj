(ns playground.generator.core
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre :refer [info error]]
            [me.raynes.fs :as fs]

            [playground-samples-parser.fs :as samples-fs]
            ;[playground-samples-parser.sample-parser :as samples-parser]

            [playground.generator.parser.group-parser :as group-parser]
            [playground.generator.utils :refer [copy-dir]]
            [playground.db.request :as db-req]
            [playground.repo.git :as git]))

;;============== component
(defrecord Generator [conf repos db notifier]
  component/Lifecycle

  (start [this]
    (timbre/info "Generator start")
    (assoc this :conf conf :repos repos))

  (stop [this]
    (timbre/info "Generator stop")
    (dissoc this :conf)))

(defn new-generator [conf repos]
  (map->Generator {:conf conf :repos repos}))

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

(defn check-repository [db repo db-repo]
  ;(info "Check repository: " @repo db-repo (merge db-repo @repo))
  (if (and db-repo (fs/exists? (:dir @repo)))
    (do
      (swap! repo (fn [repo] (merge db-repo repo {:git (git/get-git (git-path (repo-path repo)))})))
      (info (str "Repository \"" (:name @repo) "\" - OK")))
    (do
      (info (str "Repository \"" (:name @repo) "\" - sync..."))
      (try
        (download-repo repo)
        (swap! repo (fn [repo] (merge db-repo repo {:git (git/get-git (git-path (repo-path repo)))
                                                    :id  (db-req/add-project<! db {:name (:name repo)})})))
        (info (str "Repository \"" (:name @repo) "\" - OK"))
        (catch Exception e
          (info (str "Repository \"" (:name @repo) "\" - ERROR, check repository's settings " e)))))))

(defn check-repositories [generator db]
  (info "Synchronize repositories...")
  (let [repos (:repos generator)
        db-repos (db-req/projects db)
        get-repo-by-name-fn (fn [repo db-repos]
                              (first (filter #(= (:name repo) (:name %)) db-repos)))]
    (info (count db-repos) (pr-str db-repos))
    (doseq [repo repos]
      (check-repository db repo (get-repo-by-name-fn @repo db-repos)))))


;;============ remove branches
(defn- remove-branch [db branch]
  (db-req/delete-samples! db {:version_id (:id branch)})
  (db-req/delete-groups! db {:version_id (:id branch)})
  (db-req/delete-version! db {:id (:id branch)}))

(defn need-remove-branch? [db-branch actual-branches]
  (every? #(not= (:key db-branch) (:key %)) actual-branches))

(defn branches-for-remove [actual-branches db-branches]
  (let [removed-branches (filter #(need-remove-branch? % actual-branches) db-branches)]
    removed-branches))


;;============ update branches

(defn build-branch [db repo branch path versions]
  (prn "Build branch: " branch path)
  (let [groups (group-parser/groups path)
        groups2 (samples-fs/groups path)
        version-id (db-req/add-version<! db {:key        (:key branch)
                                             :commit     (:commit branch)
                                             :project_id (:id @repo)
                                             :hidden     true})]
    (prn "Path: " path)
    (prn "Groups: " (map :name groups))
    (prn "Groups2: " (map :name groups2))
    (doseq [group groups]
      (let [group-id (db-req/add-group<! db {:version_id  version-id
                                             :index       (:index group)
                                             :name        (:name group)
                                             :url         (:path group)
                                             :root        (:root group)
                                             :hidden      (:hidden group)
                                             :description (:description group)
                                             :cover       (:cover group)})]
        (prn "Group: " group-id (:name group))
        (db-req/add-samples! db group-id version-id (:samples group))))
    (let [old-versions (filter #(and (= (:key %) (:key branch))
                                     (not= (:id %) version-id)) versions)]
      (prn "Delete old versions: " old-versions)
      (doseq [version old-versions]
        (remove-branch db version)))
    (db-req/show-version! db {:project_id (:id @repo) :id version-id})))

(defn update-branch [db repo branch versions]
  (prn)
  (prn "Update branch: " branch)
  (let [path (version-path @repo (:key branch))
        git-path (git-path path)]
    (fs/delete-dir path)
    ;(fs/copy-dir (repo-path @repo) path)
    (copy-dir (repo-path @repo) path)

    (let [git-repo (git/get-git git-path)]
      (git/checkout git-repo (:key branch))
      (git/pull git-repo @repo)
      (build-branch db repo branch path versions))))

(defn need-update-branch? [branch db-branches]
  (let [db-branch (first (filter #(= (:key branch) (:key %)) db-branches))]
    (or (nil? db-branch) (not= (:commit branch) (:commit db-branch)))))

(defn branches-for-update [actual-branches db-branches]
  (let [update-branches (filter #(need-update-branch? % db-branches) actual-branches)]
    update-branches))

(defn update-repository [db repo]
  (info "update repo: " (:git @repo))
  (git/fetch repo)
  (fs/mkdirs (versions-path @repo))
  (let [actual-branches (git/branch-list (:git @repo))
        db-branches (db-req/versions db {:project_id (:id @repo)})
        updated-branches (branches-for-update actual-branches db-branches)
        removed-branches (branches-for-remove actual-branches db-branches)]
    (info "Actual branches: " (pr-str (map :key actual-branches)))
    (info "DB branches: " (pr-str (map :key db-branches)))
    (info "Updated branches: " (pr-str (map :key updated-branches)))
    (info "Removed branches: " (pr-str (map :key removed-branches)))
    (doseq [branch removed-branches]
      (remove-branch db branch))
    (doseq [branch updated-branches]
      (update-branch db repo branch db-branches))))