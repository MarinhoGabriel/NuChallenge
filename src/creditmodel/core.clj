(ns creditmodel.core
  (:require
    [creditmodel.db.database :as db]
    [creditmodel.db.schema :as schemata]))

; Database connecton
(def connection (db/open-connection))

(defn run "Runs the application doing all its functionalities"
  []
  ;; Creating the schemata
  (schemata/create-schemata connection))

; Running the application
(run)