(ns br.com.marinho.creditmodel.core.db.database
  (:use clojure.pprint)
  (:require [datomic.api :as datomic]))

(defn open-connection "Creates the database and connects to it."
  [uri]
  (datomic/create-database uri)
  (datomic/connect uri))

(defn drop-database "Drops the database."
  [uri]
  (datomic/delete-database uri))