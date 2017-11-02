(ns playground.web.helpers)

;; component
(defn get-db [request] (-> request :component :db))

(defn get-redis [request] (-> request :component :redis))


;; config
(defn get-redis-queue [request] (-> (get-redis request) :config :queue))


;; app
(defn get-repo [request] (-> request :app :repo))

(defn get-version [request] (-> request :app :version))

(defn get-sample [request] (-> request :app :sample))

(defn get-templates [request] (-> request :app :templates))

(defn get-repos [request] (-> request :app :repos))

(defn get-tags [request] (-> request :app :tags))

(defn get-all-tags [request] (-> request :app :all-tags))

(defn get-data-sets [request] (-> request :app :data-sets))

(defn get-all-data-sets [request] (-> request :app :all-data-sets))

;; pagination
(defn get-pagination [request] (-> request :app :page))

;; session
(defn get-user [request] (-> request :session :user))

(defn get-safe-user [request] (dissoc (get-user request) :salt :password :session-id :session :create-date))


;; all data
(defn get-app-data [request] (merge (:app request)
                                    (:session request)))