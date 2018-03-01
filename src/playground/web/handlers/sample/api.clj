(ns playground.web.handlers.sample.api
  (:require
    ;; comp
    [playground.db.request :as db-req]
    [playground.redis.core :as redis]
    ;; handlers
    [playground.web.handlers.sample.pages :as sample-handlers]
    ;; web
    [playground.web.helpers :refer :all]
    [playground.web.utils :as web-utils :refer [response]]
    ;; misc
    [clojure.string :as string]
    [clojure.java.jdbc :as jdbc]
    ;;spec
    [clojure.spec.alpha :as s]
    [playground.spec.sample :as sample-spec]
    [clojure.set :as set]
    [ring.util.response :refer [redirect]]
    [playground.data.tags :as tags-data]))


(defn run [request]
  (let [code (-> request :params :code)
        style (-> request :params :style)
        markup (-> request :params :markup)
        styles (-> request :params :styles (string/split #","))
        scripts (-> request :params :scripts (string/split #","))
        data {:name              "Default name"
              :tags              []
              :short-description "Default short desc"

              :scripts           scripts
              :styles            styles

              :markup            markup
              :code              code
              :style             style}]
    ;(response (render-file "templates/sample.selmer" data))
    (sample-handlers/show-sample-iframe-by-sample data)))


(defn fork [request]
  ;(prn "Fork: " (-> request :session :user) (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (web-utils/sample-hash (get-db request))
        sample* (assoc sample
                  :url hash
                  :version 0
                  :owner-id (-> request :session :user :id))]
    (let [id (db-req/add-sample! (get-db request) sample*)]
      (db-req/update-version-user-samples-latest! (get-db request) {:latest  true
                                                                    :url     hash
                                                                    :version 0})
      (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id]))
    (future (db-req/update-tags-mw! (get-db request)))
    (response {:status   :ok
               :hash     hash
               :version  0
               :owner-id (:id (get-user request))})))


(defn save [request]
  ;(prn "Save: " (-> request :params :sample))
  (let [sample (-> request :params :sample)
        hash (:url sample)
        db-sample (when (and hash (seq hash))
                    (db-req/sample-template-by-url (get-db request) {:url hash}))]
    (if (and db-sample
             (nil? (:template-id db-sample))
             (nil? (:version-id db-sample))
             (= (:id (get-user request)) (:owner-id db-sample)))
      (let [version (:version db-sample)
            new-version (inc version)
            sample* (assoc sample :version new-version
                                  :owner-id (-> request :session :user :id))
            ;id (db-req/add-sample! (get-db request) sample*)
            id (jdbc/with-db-transaction [conn (:db-spec (get-db request))]
                                         (let [id (db-req/add-sample! conn sample*)]
                                           ;(db-req/copy-visits! conn {:new-sample-id id
                                           ;                           :old-sample-id (:id db-sample)})
                                           ;(db-req/set-sample-views! conn {:id    id
                                           ;                                :views (:views db-sample)})
                                           (db-req/update-all-user-samples-latest! conn {:latest  false
                                                                                         :url     hash
                                                                                         :version new-version})
                                           (db-req/update-version-user-samples-latest! conn {:latest  true
                                                                                             :url     hash
                                                                                             :version new-version})
                                           (db-req/update-sample-views-from-canonical-visits! conn {:url     (:url sample)
                                                                                                    :repo-id (:repo-id sample)})
                                           id))]
        (redis/enqueue (get-redis request) (-> (get-redis request) :config :preview-queue) [id])
        (future (db-req/update-tags-mw! (get-db request)))
        (response {:status   :ok
                   :hash     hash
                   :version  new-version
                   :owner-id (:id (get-user request))}))
      (fork request))))


(defn add-default-data [sample]
  (assoc {}
    :name (or (:name sample) "Export chart")
    :description (or (:description sample) "")
    :short-description (or (:short-description sample) "")

    :style (or (:style sample) "html, body, #container {\n    width: 100%;\n    height: 100%;\n    margin: 0;\n    padding: 0;\n}")
    :markup (or (:markup sample) "<div id=\"container\"></div>")
    :code (or (:code sample) "")

    :code-type (or (:code-type sample) "js")
    :markup-type (or (:markup-type sample) "html")
    :style-type (or (:style-type sample) "css")

    :styles (if (seq (:styles sample))
              (:styles sample)
              ["https://cdn.anychart.com/releases/8.1.0/css/anychart-ui.min.css"])

    :scripts (if (seq (:scripts sample))
               (:scripts sample)
               ["https://cdn.anychart.com/releases/v8/js/anychart-bundle.min.js" "https://cdn.anychart.com/releases/8.1.0/js/anychart-ui.min.js"])
    :url (:url sample)))


;; for chart editor to show embed window
(defn export [request]
  ;(clojure.pprint/pprint (dissoc request :component))
  (let [hash (web-utils/sample-hash (get-db request))

        check-coll-fn (fn [data key]
                        (update data key #(if (coll? %)
                                            %
                                            (if % [%] []))))

        sample (-> (:params request)
                   (assoc :url hash)
                   (set/rename-keys {"scripts[]" :scripts
                                     "tags[]"    :tags
                                     "styles[]"  :styles})
                   (check-coll-fn :scripts)
                   (check-coll-fn :styles)
                   (check-coll-fn :tags)
                   db-req/underscore->dash
                   add-default-data)

        sample (update sample :tags (fn [tags] (concat tags (tags-data/get-tags-by-code (:code sample)))))]
    ;(prn (-> request :session :user))
    ;(prn "sample" sample)
    ;(prn "explain" (s/explain ::sample-spec/sample sample))
    (if (s/valid? ::sample-spec/sample sample)
      (if (-> request :session :user)
        (let [res (fork (assoc-in request [:params :sample] sample))
              hash (-> res :body :hash)]
          (redirect (str "/" hash "?export")))
        (response "Error: no session user"))
      (response "Error: bad sample arguments!"))))