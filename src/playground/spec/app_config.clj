(ns playground.spec.app-config
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

;;=================== App .toml config file spec ==================
(s/def ::mode #{"full" "web" "generator" "preview-generator"})

(s/def ::port (s/and int? pos?))
(s/def ::web (s/keys :req-un [::port]))

(s/def ::name string?)
(s/def ::user string?)
(s/def ::password string?)
(s/def ::host string?)
(s/def ::db (s/keys :req-un [::port ::host ::name ::user ::password]))

(s/def :redis/db (s/and int? #(>= % 0)))
(s/def ::redis (s/keys :req-un [::port ::host :redis/db]))

(s/def ::token string?)
(s/def ::channel string?)
(s/def ::username string?)
(s/def ::domsin string?)
(s/def ::tag string?)
(s/def ::slack (s/keys :req-un [::token ::channel ::username ::domain ::tag]))
(s/def ::notifications (s/keys :req-un [::slack]))

(s/def ::dir string?)
(s/def ::url-prefix string?)
(s/def ::generate-preview boolean?)
(s/def ::type #{"ssh" "https"})

(s/def :ssh/ssh string?)
(s/def ::secret-key string?)
(s/def ::public-key string?)
(s/def ::passphrase string?)
(s/def ::ssh (s/keys :req-un [:ssh/ssh ::secret-key ::public-key ::passphrase]))

(s/def ::login string?)
(s/def :https/https string?)
(s/def ::https (s/keys :req-un [:https/https ::login ::password]))
(s/def ::repository (s/and (s/keys :req-un [::name ::dir ::url-prefix ::generate-preview ::type]
                                   :opt-un [::https ::ssh])
                           #(or (:https %) (:ssh %))))

(s/def ::repositories (s/coll-of ::repository))

(s/def ::config (s/keys :req-un [::mode ::web ::db ::redis ::notifications ::repositories]))

