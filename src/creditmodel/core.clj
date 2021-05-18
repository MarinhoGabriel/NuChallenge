(ns creditmodel.core
  (:require
    [creditmodel.db.database :as db]
    [creditmodel.db.schema :as schemata]
    [creditmodel.controller :as controller]))

; Dropping database if exists
(db/drop-database)

; Database connecton
(def connection (db/open-connection))

(defn run "Runs the application doing all its functionalities"
  []
  ;; Creating the schemata
  (schemata/create-schemata connection)

  ;; Inserting clients in database
  (controller/save-client "Gabriel Marinho" (str (long (rand 100000000000))) "gabriel.marinho@gmail.com" connection)
  (controller/save-client "Vitoria G." (str (long (rand 100000000000))) "vitoria.g@gmail.com" connection)
  (controller/save-client "Yasmin S." (str (long (rand 100000000000))) "yasmin.s@gmail.com" connection))

; Running the application
(run)