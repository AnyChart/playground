(ns playground.web.auth-base)

;; actions
;(def ^:const signin 1)
;(def ^:const signup 2)
;(def ^:const create-sample 4)
;(def ^:const save-sample 8)
;(def ^:const fork-sample 16)
;(def ^:const view-sample-editor 32)
;(def ^:const view-sample-standalone 64)
;(def ^:const view-sample-iframe 128)

(def ^:const permissions {:signin                 1
                          :signup                 2
                          :create-sample          4
                          :save-sample            8
                          :fork-sample            16
                          :view-sample-editor     32
                          :view-sample-standalone 64
                          :view-sample-iframe     128})

(def ^:const anonymous-perms (apply bit-or (vals permissions)))

(def ^:const base-perms (bit-xor anonymous-perms
                                 (:signin permissions)
                                 (:signup permissions)))

(defn can [user action]
  (pos? (bit-and (:permissions user) (action permissions))))