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
  (controller/save-client "Gabriel Marinho" "00000000000" "gabriel.marinho@gmail.com" connection)
  (controller/save-client "Vitoria G." "11111111111" "vitoria.g@gmail.com" connection)
  (controller/save-client "Yasmin S." "22222222222" "yasmin.s@gmail.com" connection)

  ;; Adding cards to the clients
  (controller/add-card-to-client "00000000000" connection)
  (controller/add-card-to-client "11111111111" connection)
  (controller/add-card-to-client "22222222222" connection))

; Running the application
(run)