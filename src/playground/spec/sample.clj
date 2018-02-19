(ns playground.spec.sample
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::description string?)
(s/def ::short-description string?)

(s/def ::tags (s/coll-of string?))
(s/def ::exports (s/coll-of string?))

(s/def ::scripts (s/coll-of string?))
(s/def ::local-scripts (s/coll-of string?))
(s/def ::styles (s/coll-of string?))

(s/def ::code-type #{"js" "cljs" "coffee"})
(s/def ::code string?)

(s/def ::markup-type #{"html" "jade"})
(s/def ::markup string?)

(s/def ::style-type #{"css" "less" "sass"})
(s/def ::style string?)

(s/def ::url string?)

(s/def ::sample (s/keys :req-un [::name
                                 ::description
                                 ::short-description

                                 ::tags
                                 ::scripts
                                 ::styles

                                 ::code-type
                                 ::code
                                 ::markup-type
                                 ::markup
                                 ::style-type
                                 ::style

                                 ::url]))

(s/def ::samples (s/coll-of ::sample))