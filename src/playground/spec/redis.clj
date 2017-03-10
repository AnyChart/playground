(ns playground.spec.redis
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(s/def ::host string?)
(s/def ::port int?)
(s/def ::db int?)
(s/def ::queue string?)
(s/def ::config (s/keys :req-un [::host ::port ::db ::queue]))

(s/def ::spec (s/keys :req-un [::host ::port ::db]))
(s/def ::pool map?)
(s/def ::conn (s/keys :req-un [::pool ::spec]))

(s/def ::redis (s/keys :req-un [::config ::conn]))