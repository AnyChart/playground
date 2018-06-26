(ns playground.elastic.consts)

;; =====================================================================================================================
;; Constants elastic init
;; =====================================================================================================================
(def ^:const elastic-max-result-window 50000)
(def ^:const elastic-bulk-samples-count 20000)


(def mapping-default {:properties
                      {:description       {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :create-date       {:type "date"},
                       :tags              {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :short-description {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :name              {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :version-id        {:type "long"},
                       :likes             {:type "long"},
                       :repo-name         {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :full-url          {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :latest            {:type "boolean"},
                       :preview           {:type "boolean"},
                       :id                {:type "long"},
                       :version-name      {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :url               {:type   "text",
                                           :fields {:keyword {:type "keyword", :ignore_above 256}}},
                       :version           {:type "long"},
                       :views             {:type "long"}}})


(def mapping {:properties
              {:description       {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :create-date       {:type "date"}
               :tags              {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :tags-kw           {:type       "keyword"
                                   :normalizer :lowercase_normalizer}
               :short-description {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :name              {:type   "text"
                                   :fields {:keyword {:type "keyword" :ignore_above 256}}}
               :name-kw           {:type       "keyword"
                                   :normalizer :lowercase_normalizer}
               :version-id        {:type "long"}
               :likes             {:type "long"}
               :repo-name         {:type "keyword"}
               :full-url          {:type "keyword"}
               :fullname          {:type "keyword"}
               :latest            {:type "boolean"}
               :preview           {:type "boolean"}
               :id                {:type "long"}
               :version-name      {:type "keyword"}
               :url               {:type "keyword"}
               :version           {:type "long"}
               :views             {:type "long"}}})


(def sample {:description       ""
             :create-date       "2018-02-22T08:55:35Z"
             :tags              ["Tag Cloud" "Weighted List Chart" "Word Cloud"]
             :short-description ""
             :name              "BCT Tag Cloud Chart 13"
             :version-id        246
             :likes             0
             :repo-name         "docs"
             :full-url          "/docs/8.1.0/samples/BCT_Tag_Cloud_Chart_13"
             :latest            true
             :preview           true
             :id                133737
             :version-name      "8.1.0"
             :url               "samples/BCT_Tag_Cloud_Chart_13"
             :version           0
             :views             0})