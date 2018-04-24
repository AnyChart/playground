(ns playground.data.modules
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [playground.data.geodata :as geo]))


;;======================================================================================================================
;; Modules.json creation for 7 versions
;;======================================================================================================================

(def vs ["7.14.4"
         "7.14.3"
         "7.14.0"
         "7.13.1"
         "7.13.0"
         "7.12.0"
         "7.11.1"
         "7.11.0"
         "7.10.1"
         "7.10.0"
         "7.9.1"
         "7.9.0"
         "7.8.0"
         "7.7.0"
         "7.6.0"
         "7.5.1"
         "7.5.0"
         "7.4.1"
         "7.4.0"
         "7.3.1"
         "7.3.0"
         "7.2.0"
         "7.1.1"
         "7.1.0"
         "7.0.0"]
  )

(defn go []
  (println (string/join " && " (map (fn [v]
                                      (str "mkdir -p /apps/static/cdn/releases/" v "/js")
                                      )
                                    vs)))

  )




(def bundle {:type :bundle
             :name "AnyChart Bundle"
             :desc "AnyChart Bundle module"})

(def anychart {:type :bundle
               :name "AnyChart module"
               :desc "AnyChart module"})

(def anygantt {:type :bundle
               :name "AnyGantt module"
               :desc "AnyGantt module"})

(def anymap {:type :bundle
             :name "AnyMap module"
             :desc "AnyMap module"})

(def anystock {:type :bundle
               :name "AnyStock module"
               :desc "AnyStock module"})

(def anychart-ui {:type :feature
                  :icon "/_design/img/upload/charts/types/area-chart.svg"
                  :name "Common UI"
                  :desc "Context menu, range picker, range selector and preloader module."})

(def data-adapter {:type :feature
                   :name "Data Adapter"
                   :icon "/_design/img/upload/charts/types/area-chart.svg"
                   :desc "AnyChart data adapter module."
                   :docs "https://docs.anychart.com/Working_with_Data/Data_Adapter/"})

(def chart-editor {:type :feature
                   :name "Chart Editor"
                   :docs ""
                   :icon "/_design/img/upload/charts/types/area-chart.svg"
                   :desc "AnyChart chart editor module."})

(def graphics {:type :bundle
               :name "Graphics module"
               :docs ""
               :desc "AnyChart Graphics module."})


;; https://cdn.anychart.com/js/8.2.1/anychart-bundle.min.js


(def themes1 {:v6 {:name "AnyChart v6 theme"}})
(def themes2 {:light_blue      {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Light Blue",
                                :desc "Light Blue Theme. "},
              :dark_provence   {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Dark Provence",
                                :desc "Dark Provence Theme. Dark backgrounds and soft colors."},
              :light_provence  {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Light Provence",
                                :desc "Light Provence Theme. Light backgrounds and soft colors."},
              :morning         {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Morning",
                                :desc "Morning Theme. Light background and vivid colors."},
              :coffee          {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Coffee",
                                :desc "Coffee Theme. Dimmed brown and brownish colors."},
              :monochrome      {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Monochrome",
                                :desc "Monochrome Theme. Light background and grayish colors."},
              :wines           {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Wines",
                                :desc "Wines Theme. Dark background and lively colors."},
              :dark_earth      {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Dark Earth",
                                :desc "Dark Earth Theme. Dimmed green and orange colors."},
              :dark_glamour    {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Dark Glamour",
                                :desc "Dark Glamour Theme. Dark backgrounds and vibrant pink and magenta."},
              :pastel          {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Pastel",
                                :desc "Pastel Theme. Light background and pastel colors."},
              :dark_turquoise  {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Dark Turquoise",
                                :desc "Dark Turquoise Theme. Dark backgrounds and and soft turquoise colors."},
              :light_glamour   {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Light Glamour",
                                :desc "Light Glamour Theme. Light backgrounds and vibrant pink and magenta."},
              :sea             {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Sea",
                                :desc "Sea Theme. Light background and thalassian palette."},
              :dark_blue       {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Dark Blue",
                                :desc "Dark Blue Theme. Dimmed blue and blueish colors."},
              :light_earth     {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Light Earth",
                                :desc "Light Earth Theme. Dimmed light blue and blueish colors."},
              :light_turquoise {:icon "/_design/img/upload/charts/types/area-chart.svg",
                                :name "Light Turquoise",
                                :desc "Light Turquoise Theme. Light backgrounds and and soft turquoise colors."}})




(def v7-0-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   ;:locales {}
   :geodata geo/data
   :themes  {}
   })


(def v7-1-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   :geodata geo/data
   :themes  {}
   })


(def v7-1-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   :geodata geo/data
   :themes  {}
   })


(def v7-2-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   :themes  {}
   })


(def v7-3-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt}
   :geodata geo/data
   :themes  {}
   })


(def v7-3-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt}
   :geodata geo/data
   :themes  {}
   })


(def v7-4-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt}
   :geodata geo/data
   :themes  {}
   })


(def v7-4-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   :geodata geo/data
   :themes  {}
   })


(def v7-5-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt}
   :geodata geo/data
   :themes  {}
   })


(def v7-5-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart}
   :geodata geo/data
   :themes  {}
   })


(def v7-6-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap}
   :geodata geo/data
   :themes  themes1
   })


(def v7-7-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock}
   :geodata geo/data
   :themes  themes1
   })

(def v7-8-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock}
   :geodata geo/data
   :themes  themes1
   })

(def v7-9-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock}
   :geodata geo/data
   :themes  themes1
   })

(def v7-9-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock}
   :geodata geo/data
   :themes  themes2
   })


(def v7-10-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui}
   :geodata geo/data
   :themes  themes2
   })

(def v7-10-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui}
   :geodata geo/data
   :themes  themes2
   })

(def v7-11-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui}
   :geodata geo/data
   :themes  themes2
   })


(def v7-11-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter}
   :geodata geo/data
   :themes  themes2
   })


(def v7-12-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter}
   :geodata geo/data
   :themes  themes2
   })


(def v7-13-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter
             :chart-editor    chart-editor}
   :geodata geo/data
   :themes  themes2
   })

(def v7-13-1
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter
             :chart-editor    chart-editor}
   :geodata geo/data
   :themes  themes2
   })

(def v7-14-0
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter
             :chart-editor    chart-editor
             :graphics        graphics}
   :geodata geo/data
   :themes  themes2
   })

(def v7-14-3
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter
             :chart-editor    chart-editor}
   :geodata geo/data
   :themes  themes2
   })

(def v7-14-3
  {:modules {:anychart-bundle bundle
             :anychart        anychart
             :anygantt        anygantt
             :anymap          anymap
             :anystock        anystock

             :anychart-ui     anychart-ui
             :data-adapter    data-adapter
             :chart-editor    chart-editor}
   :geodata geo/data
   :themes  themes2
   })


(defn go []
  (json/parse-string (slurp "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.1.0.json") true))



(defn generate []
  (try
    (let [data (json/generate-string v7-7-0)]
      (spit
        (io/file (str "/media/ssd/sibental/playground-data/MODULES V7 GENERATION/modules" "7.0.0" ".json"))
        data)
      data)
    (catch Exception e (println e))))


(defn gdata []
  (let [data (json/parse-string (slurp "/media/ssd/sibental/playground-data/MODULES V8 GENERATION/modules-8.1.0.json") true)
        data (:geodata data)
        ;data (dissoc data :usa_states :canada_states :china_areas)
        ]
    (println data)
    (println (keys data))
    data))