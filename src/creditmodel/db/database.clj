(ns creditmodel.db.database
  (:use clojure.pprint)
  (:require [datomic.api :as datomic]))

; Database URI used to open the connection
(def uri "datomic:dev://localhost:4334/creditmodel")

(defn open-connection "Creates the database and connects to it."
  []
  (datomic/create-database uri)
  (datomic/connect uri))

(defn drop-database "Drops the database."
  []
  (datomic/delete-database uri))