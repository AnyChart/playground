(ns playground.web.handlers.generator-handlers
  (:require
    ;; comp
    [playground.redis.core :as redis]
    [playground.db.request :as db-req]
    [playground.db.migration :as migration]
    ;; web
    [playground.web.helpers :refer :all]
    [playground.web.utils :as web-utils :refer [response]]))

(defn update-repo [request]
  (let [repo (get-repo request)]
    (redis/enqueue (get-redis request)
                   (get-redis-queue request)
                   (:name repo))
    (response (str "Start updating: " (:name repo)))))

(defn- generate-previews [samples request]
  (let [ids (map :id samples)]
    (if (seq ids)
      (do (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) ids)
          (response (str "Start generate previews for " (count samples) " samples: "
                         (clojure.string/join ", " (map :name samples)))))
      "All samples have previews")))

(defn user-previews [request]
  (generate-previews (db-req/user-samples-without-preview (get-db request)) request))

(defn repo-previews [request]
  (generate-previews (db-req/repo-samples-without-preview (get-db request)) request))

(defn refresh-views [request]
  (future (migration/refresh-views-from-canonical-visits (get-db request)))
  (str "Update samples views!"))