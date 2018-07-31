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


(defn update-versions []
  (POST "/_admin_/versions"
        {:params        {:project (-> @state :project)}
         :handler       #(do
                           (println %)
                           (swap! state assoc :versions %)
                           (swap! state assoc :version (first %)))
         :error-handler #(println %)}))


(defn change-project [project]
  (println "change-project:" project)
  (swap! state assoc :project project)
  (update-versions))


(defn delete-version []
  (POST "/_admin_/delete"
        {:params        {:project (-> @state :project)
                         :version (-> @state :version)}
         :handler       #(do
                           (println %)
                           (js/alert "Delete version!")
                           (update-versions))
         :error-handler #(do
                           (println %)
                           (js/alert "Error occured, see console output!"))}))


(defn rebuild-version []
  (POST "/_admin_/rebuild"
        {:params        {:project (-> @state :project)
                         :version (-> @state :version)}
         :handler       #(do
                           (println %)
                           (js/alert "Start rebuilding!")
                           (update-versions))
         :error-handler #(do
                           (println %)
                           (js/alert "Error occured, see console output!"))}))


(defn change-version [version]
  (swap! state assoc :version version))


(rum/defc project-select < rum/reactive []
  [:div.admin-panel
   [:form

    [:b [:label {:for "project-select"} "Select project"]]
    [:div.form-row
     [:select.form-control#project-select {:value     (or (:project (rum/react state)) "")
                                           :on-change #(change-project (-> % .-target .-value))}
      (for [repo (:projects (rum/react state))]
        [:option {:key   repo
                  :value repo} repo])]
     [:a.btn.btn-success {:role "button"
                          :href (str "/" (:project (rum/react state)) "/_update_")} "Update versions"]]
    [:br]
    [:div.form-group
     [:b [:label {:for "version-select"} "Select version"]]
     [:div.form-row
      [:select.form-control#version-select {:value     (or (:version (rum/react state)) "")
                                            :on-change #(change-version (-> % .-target .-value))}
       (for [version (:versions (rum/react state))]
         [:option {:key   version
                   :value version} version])]
      [:button.btn.btn-danger {:type     "button"
                               :on-click delete-version} "Delete"]
      [:button.btn.btn-primary {:type     "button"
                                :on-click rebuild-version} "Rebuild"]]]

    [:br]
    [:div.alert.alert-info
     "This action is used to update AnyChart develop versions in PG editor."
     [:br] [:br]
     [:a.btn.btn-success {:role "button"
                          :href "/_update_anychart_versions_"} "Update AnyChart versions"]]
    [:a.btn.btn-link {:role "button"
                      :href "/tags/index"} "Show tags stat"]]])


(defn ^:export init [data]
  (let [data (t/read (t/reader :json) data)
        repos (map :name (:repos data))]
    (reset! state {:projects repos
                   :project  (first repos)})
    (update-versions)
    (println repos)
    (rum/mount (project-select)
               (.getElementById js/document "main-container"))))