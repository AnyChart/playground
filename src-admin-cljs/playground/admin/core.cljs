(ns playground.admin.core
  (:require [rum.core :as rum]
            [cognitect.transit :as t]
            [playground.utils.utils :as utils]
            [ajax.core :refer [GET POST]]))


(enable-console-print!)


(def state (atom {:projects []
                  :project  nil
                  :versions []
                  :version  nil}))


(defn get-project-by-name [project-name]
  (first (filter #(= project-name (:name %)) (:projects @state))))


(defn update-versions []
  (POST "/_admin_/versions"
        {:params        {:project-id (-> @state :project :id)}
         :handler       #(swap! state assoc :versions %)
         :error-handler #(println %)}))


(defn change-project [project]
  (println "change-project:" project)
  (swap! state assoc :project (get-project-by-name project))
  (update-versions))


(defn change-version [version]

  )


(rum/defc project-select < rum/reactive []
  [:div
   [:h5 "Version management panel"]
   [:form
    [:div.form-group
     [:label {:for "project-select"} "Select project"]
     [:br]
     [:select.custom-select#project-select {:on-change #(change-project (-> % .-target .-value))}
      (for [repo (:projects (rum/react state))]
        [:option {:key   (:name repo)
                  :value (:name repo)} (:name repo)])]
     [:a.btn.btn-success {:role "button"
                          :href (str "/" (:name (:project (rum/react state))) "/_update_")} "Update versions"]    ]

    [:label {:for "version-select"} "Select version"]
    [:br]
    [:select.custom-select#version-select {:on-change #(change-version (-> % .-target .-value))}
     (for [version (:versions (rum/react state))]
       [:option {:key   (:name version)
                 :value (:name version)} (:name version)])]
    [:button.btn.btn-danger "Delete"]
    [:button.btn.btn-primary "Rebuild"]

    [:br]
    [:div.alert.alert-primary
     "This action is used to update AnyChart develop versions in PG editor."
     [:br]
     [:a.btn.btn-success {:role "button"
                          :href "/_update_anychart_versions_"} "Update AnyChart versions"]]
    [:a.btn.btn-link {:role "button"
                      :href "/tags/index"} "Show tags stat" ]

    ]
   ])


(rum/defc main-comp []
  [:div "asdf"])


(defn ^:export init [data]
  (let [data (t/read (t/reader :json) data)
        repos (:repos data)]
    (reset! state {:projects (:repos data)
                   :project  (first repos)})
    (update-versions)
    (println data)
    (rum/mount (project-select)
               (.getElementById js/document "main-container"))))