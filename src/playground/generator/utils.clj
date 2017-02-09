(ns playground.generator.utils
  (:require [clojure.java.shell :refer [sh with-sh-env with-sh-dir]]
            [clojure.string :refer [split]]))

(defn run-sh [& command]
  (apply sh command))

(defn copy-dir
  "Copy dir with permissions, raynes.fs changes executable files permisssions when copying"
  [repo target-path]
  (run-sh "rm" "-rf" target-path)
  (run-sh "cp" "-r" repo target-path))