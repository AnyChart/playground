(ns playground.spec.sample
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(s/def ::name string?)
(s/def ::description string?)
(s/def ::short_description string?)

(s/def ::show_on_landing boolean?)
(s/def ::tags (s/coll-of string?))
(s/def ::exports (s/coll-of string?))

(s/def ::scripts (s/coll-of string?))
(s/def ::local_scripts (s/coll-of string?))
(s/def ::styles (s/coll-of string?))

(s/def ::code_type #{"js" "cljs" "coffee"})
(s/def ::code string?)

(s/def ::markup_type #{"html" "jade"})
(s/def ::markup string?)

(s/def ::style_type #{"css" "less" "sass"})
(s/def ::style string?)

(s/def ::url string?)

(s/def ::sample (s/keys :req-un [::name
                                 ::description
                                 ::short_description

                                 ::show_on_landing
                                 ::tags
                                 ::exports

                                 ::scripts
                                 ::local_scripts
                                 ::styles

                                 ::code_type
                                 ::code
                                 ::markup_type
                                 ::markup
                                 ::style_type
                                 ::style

                                 ::url]))

(s/def ::samples (s/coll-of ::sample))