(ns playground.generator.parser-old.sample-parser
  (:require [net.cgrand.enlive-html :as html]
            [taoensso.timbre :refer [info error]]
            [clojure.string :as s :refer (trim-newline)]
            [clojure.java.io :refer [file]]))

(defn check-export [sample]
  (let [export (:exports sample)
        pattern (re-pattern (str export "\\s*="))]
    (empty? (re-find pattern (:code sample)))))

(defn- fix-exports [sample]
  (if (and (:exports sample) (:code sample))
    (let [export (:exports sample)
          pattern (re-pattern (str "var\\s+" export))
          new-code (-> (:code sample) (s/replace pattern export))]
      (assoc sample :code new-code))
    sample))

(defn- trim-code [code]
  (let [s (trim-newline code)]
    (loop [index 0]
      (if (= 0 (.length s))
        ""
        (let [ch (.charAt s index)]
          (if (or (= ch \newline) (= ch \return))
            (recur (inc index))
            (.. s (subSequence index (.length s)) toString)))))))

(defn- get-external-scripts [page]
  (map #(:src (:attrs %))
       (filter #(some? (:data-export (:attrs %)))
               (html/select page [:script]))))

(defn- parse-html-sample [path]
  (let [page (html/html-resource (file path))
        script-node (first (filter #(not (:src (:attrs %)))
                                   (html/select page [:script])))
        css-nodes (filter #(and (= (-> % :attrs :rel) "stylesheet")
                                (-> % :attrs :href some?))
                          (html/select page [:link]))
        code (apply str (:content script-node))
        desc (-> page
                 (html/select [:description])
                 first
                 :content
                 html/emit*)
        short-desc (-> page
                       (html/select [:short_description])
                       first
                       :content
                       html/emit*)
        exports (:x-export (:attrs script-node))]

    {:tags []
     :scripts (get-external-scripts page)
     :css_libs (map #(-> % :attrs :href) css-nodes)
     :description (apply str desc)
     :short_description (apply str short-desc)
     :is_new false
     :exports (if exports exports "chart")
     :code (clojure.string/replace code #"(?m)^[ ]{8}" "")
     :index 1000}))

(defn- parse-sample [path]
  (let [raw-content (slurp path)
        matches (re-matches #"(?s)(?m)(^\{[^\}]+\}).*" raw-content)
        meta (if matches (try (read-string (last matches))
                              (catch Exception e
                                (println (.getMessage e))
                                (error (str path "generation failed")))))]
    {:tags (:tags meta)
     :is_new (:is_new meta)
     :scripts (:scripts meta)
     :css_libs (:css_libs meta)
     :exports (:exports meta)
     :code (trim-code (clojure.string/replace raw-content #"(?s)(?m)(^\{[^\}]+\})" ""))
     :index (if (:index meta) (:index meta) 1000)
     :libs (:libs meta)
     :custom-name (:i_really_need_custom_sample_display_name meta)}))

(defn- sample-path [base-path group sample]
  (if (.exists (file (str base-path group sample)))
    (str base-path group sample)
    (str base-path group "_samples/" sample)))

(defn parse [base-path group sample]
  (let [path (sample-path base-path group sample)
        name (clojure.string/replace sample #"\.(html|sample)$" "")
        base-info (if (.endsWith path ".html")
                    (parse-html-sample path)
                    (if (.endsWith path ".sample")
                      (parse-sample path)))]
    (when base-info
      (assoc (fix-exports base-info)
        :name (if (:custom-name base-info)
                (:custom-name base-info)
                (clojure.string/replace name #"_" " "))
        :hidden (= name "Coming_Soon")
        :url (str (if (= group "/")
                    group
                    (str "/" group))
                  (clojure.string/replace name #"%" "%25"))))))
