(ns playground.generator.parser.sample-parser
  (:require [net.cgrand.enlive-html :as html]
            [taoensso.timbre :refer [info error]]
            [clojure.string :as s :refer (trim-newline)]
            [clojure.java.io :refer [file]]
            [toml.core :as toml]))

(defn- ^String trim-newline-left [^CharSequence s]
  (loop [index 0]
    (if (= 0 (.length s))
      ""
      (let [ch (.charAt s index)]
        (if (or (= ch \newline) (= ch \return))
          (recur (inc index))
          (.. s (subSequence index (.length s)) toString))))))

(defn trim-code [code]
  (-> code trim-newline trim-newline-left))

;(defn- fix-exports [sample]
;  (if (and (:exports sample) (:code sample))
;    (let [export (:exports sample)
;          pattern (re-pattern (str "var\\s+" export))
;          new-code (-> (:code sample) (s/replace pattern export))]
;      (assoc sample :code new-code))
;    sample))


(defn tag-content [envlive-page tag]
  (apply str (-> envlive-page (html/select [tag]) first :content html/emit*)))

(defn parse-html-sample [path]
  (let [page (html/html-resource (file path))
        scripts (->> (html/select page [:script])
                     (filter #(some? (:data-export (:attrs %))))
                     (map #(:src (:attrs %))))
        local-scripts (->> (html/select page [:script])
                           (filter #(nil? (:data-export (:attrs %))))
                           (map #(:src (:attrs %))))
        script-node (->> (html/select page [:script])
                         (filter #(not (:src (:attrs %))))
                         first)
        code (apply str (:content script-node))
        exports (:x-export (:attrs script-node))
        css-libs (->> (html/select page [:link])
                      (filter #(and (= (-> % :attrs :rel) "stylesheet")
                                    (-> % :attrs :href some?)))
                      (map #(-> % :attrs :href)))

        desc (tag-content page :description)
        short-desc (tag-content page :short_description)

        tags-content (->> (html/select page [:meta])
                          (filter #(= "tags" (:name (:attrs %))))
                          first :attrs :content)
        tags (if tags-content (clojure.string/replace tags-content #"'" "\"") [])

        is-new (some->> (html/select page [:meta])
                        (filter #(= "is-new" (:name (:attrs %))))
                        first :attrs :content read-string)

        index (some->> (html/select page [:meta])
                       (filter #(= "index" (:name (:attrs %))))
                       first :attrs :content read-string)]
    {:description       desc
     :short_description short-desc

     :tags              tags
     :exports           (or exports "chart")

     :scripts           scripts
     :local_scripts     local-scripts
     :styles            css-libs

     :code_type         "js"
     :code              (trim-code (clojure.string/replace code #"(?m)^[ ]{8}" ""))

     :markup_type       nil
     :markup            nil

     :style_type        nil
     :style             nil}))



(defn parse-toml-sample [path]
  (try
    (let [data (toml/read (slurp path) :keywordize)]
     {:name              (-> data :name)
      :description       (-> data :description)
      :short_description (-> data :short_description)

      :tags              (-> data :meta :tags)
      :exports           (-> data :meta :export)

      :scripts           (-> data :deps :scripts)
      :local_scripts     (-> data :deps :local_scripts)
      :styles            (-> data :deps :styles)

      :code_type         (-> data :code :type)
      :code              (-> data :code :code)

      :markup_type       (-> data :markup :type)
      :markup            (-> data :markup :code)

      :style_type        (-> data :style :type)
      :style             (-> data :style :code)})
    (catch Exception e
      (info "parse TOML error: " path e)
      nil)))

(defn- sample-path [base-path group sample]
  (if (.exists (file (str base-path group sample)))
    (str base-path group sample)
    (str base-path group "_samples/" sample)))

(defn parse [base-path group sample]
  (let [path (sample-path base-path group sample)
        name (clojure.string/replace sample #"\.(html|sample)$" "")
        base-info (cond (.endsWith path ".html") (parse-html-sample path)
                        (.endsWith path ".sample") (parse-toml-sample path))]
    (when base-info
      (assoc base-info                                      ;TODO need fix exports?  ; (fix-exports base-info)
        :name (clojure.string/replace name #"_" " ")
        :hidden (= name "Coming_Soon")
        :url (str (if (= group "/")
                    group
                    (str "/" group))
                  (clojure.string/replace name #"%" "%25"))))))
