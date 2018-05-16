(ns playground.data.consts)


(def ^:const scripts-title
  "Add any script, drag to change the order, click to edit the path.")


(def ^:const styles-title
  "Add any CSS, drag to change the order, click to edit the path.")


(def ^:const script-style-warning
  (str "This AnyChart module has different version and this conflict may lead to fatal errors. "
       "Fix this or proceed at your own risk."))


(def ^:const script-order-warning
  (str "This AnyChart module must be after main AnyChart Bundle or AnyChart Base module. "
       "Fix this or proceed at your own risk."))


(def ^:const settings-warning
  "One or several AnyChart modules have conflicting versions, it is recommended that you fix this issue.")


(def ^:const modal-window-warning
  "One or several AnyChart modules have conflicting versions, it is recommended that you fix this issue. Are you sure you want to proceed?")