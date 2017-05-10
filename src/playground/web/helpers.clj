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


;; session
(defn get-user [request] (-> request :session :user))

(defn get-cut-user [request] (dissoc (get-user request) :salt :password))